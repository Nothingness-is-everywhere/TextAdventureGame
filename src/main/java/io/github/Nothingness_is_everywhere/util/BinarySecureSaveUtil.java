package io.github.Nothingness_is_everywhere.util;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.base.ElementType;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;
import io.github.Nothingness_is_everywhere.entity.nonEntities.AbstractNonEntities;
import io.github.Nothingness_is_everywhere.entity.nonEntities.persistent.AbstractPersistentEffect;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 二进制加密存档工具类（优化版）
 * 采用索引+数据双文件结构，支持高效CRUD操作，避免全量加载
 */
public class BinarySecureSaveUtil {
    // AES密钥（16字节=128位）
    private static final byte[] SECRET_KEY_BYTES = "GameSaveKey12345".getBytes(StandardCharsets.UTF_8);
    private static final String ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String INDEX_EXTENSION = ".index";
    private static final String DATA_EXTENSION = ".data";
    // 索引条目固定长度：UUID(16字节) + 偏移量(8字节) + 数据长度(4字节) = 28字节
    private static final int DESC_LENGTH = 20; // 描述字符数
    private static final int DESC_BYTE_LENGTH = DESC_LENGTH * 3; // UTF-8最多3字节/汉字
    private static final int INDEX_ENTRY_LENGTH = 16 + DESC_BYTE_LENGTH + 8 + 4; // 88字节
    // 读写锁保证线程安全
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 添加对象到存档（自动生成ID）
     * @param obj 可序列化对象
     * @param basePath 基础路径（不含扩展名）
     * @return 生成的唯一ID
     */
    public static String add(Object obj, String basePath) throws Exception {
        if (!(obj instanceof Serializable)) {
            throw new IllegalArgumentException("对象未实现Serializable接口");
        }
        String id = UUID.randomUUID().toString();
        addWithId(id, obj, basePath);
        return id;
    }

    /**
     * 按指定ID添加对象
     * @param id 唯一标识
     * @param obj 可序列化对象
     * @param basePath 基础路径
     */
    public static void addWithId(String id, Object obj, String basePath) throws Exception {
        lock.writeLock().lock();
        try {
            // 1. 序列化并加密对象
            byte[] objBytes = serialize(obj);
            byte[] hashBytes = calculateHashBytes(objBytes);
            byte[] encryptedBytes = encrypt(objBytes);

            // 2. 写入数据文件
            String dataPath = basePath + DATA_EXTENSION;
            try (RandomAccessFile dataFile = new RandomAccessFile(dataPath, "rw")) {
                long dataOffset = dataFile.length(); // 新数据写在文件末尾
                dataFile.seek(dataOffset);

                // 数据格式：[哈希长度(int)] [哈希字节] [加密数据]
                dataFile.writeInt(hashBytes.length);
                dataFile.write(hashBytes);
                dataFile.writeInt(encryptedBytes.length);
                dataFile.write(encryptedBytes);
                String description = "无"; // 默认值设为"无"
                try {
                    // 获取类对象
                    Class<?> clazz = obj.getClass();
                    // 尝试获取无参数的getDescription()方法
                    Method method = clazz.getMethod("getDescription");
                    // 如果方法存在，调用并获取结果
                    Object result = method.invoke(obj);
                    // 确保结果不为null再转换为String
                    if (result != null) {
                        description = result.toString();
                    }
                } catch (NoSuchMethodException e) {
                    // 方法不存在，保持默认值"无"
                } catch (Exception e) {
                    // 处理其他可能的异常（如调用失败等）
                    description = "获取失败";
                }
                // 3. 写入索引文件
                String indexPath = basePath + INDEX_EXTENSION;
                try (RandomAccessFile indexFile = new RandomAccessFile(indexPath, "rw")) {
                    indexFile.seek(indexFile.length());
                    writeIndexEntry(indexFile, id, dataOffset,
                            4 + hashBytes.length + 4 + encryptedBytes.length, description);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 根据ID查询对象
     * @param id 唯一标识
     * @param basePath 基础路径
     * @return 解密并校验后的对象
     */
    public static Object get(String id, String basePath) throws Exception {
        lock.readLock().lock();
        try {
            String indexPath = basePath + INDEX_EXTENSION;
            if (!Files.exists(Paths.get(indexPath))) {
                return null;
            }

            // 1. 查找索引
            IndexEntry entry = findIndexEntry(id, indexPath);
            if (entry == null) {
                return null;
            }

            // 2. 读取数据文件对应位置
            String dataPath = basePath + DATA_EXTENSION;
            try (RandomAccessFile dataFile = new RandomAccessFile(dataPath, "r")) {
                dataFile.seek(entry.dataOffset);

                // 读取哈希
                int hashLen = dataFile.readInt();
                byte[] storedHash = new byte[hashLen];
                dataFile.readFully(storedHash);

                // 读取加密数据
                int dataLen = dataFile.readInt();
                byte[] encryptedData = new byte[dataLen];
                dataFile.readFully(encryptedData);

                // 3. 解密并校验
                byte[] decryptedData = decrypt(encryptedData);
                if (!MessageDigest.isEqual(storedHash, calculateHashBytes(decryptedData))) {
                    throw new SecurityException("数据校验失败，可能被篡改");
                }

                return deserialize(decryptedData);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 更新指定ID的对象
     * @param id 唯一标识
     * @param obj 新对象
     * @param basePath 基础路径
     * @return 是否更新成功
     */
    public static boolean update(String id, Object obj, String basePath) throws Exception {
        lock.writeLock().lock();
        try {
            // 1. 先删除旧数据（逻辑删除，仅删除索引）
            boolean deleted = delete(id, basePath);
            if (!deleted) {
                return false;
            }
            // 2. 添加新数据
            addWithId(id, obj, basePath);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 删除指定ID的对象
     * @param id 唯一标识
     * @param basePath 基础路径
     * @return 是否删除成功
     */
    public static boolean delete(String id, String basePath) throws Exception {
        lock.writeLock().lock();
        try {
            String indexPath = basePath + INDEX_EXTENSION;
            if (!Files.exists(Paths.get(indexPath))) {
                return false;
            }

            // 1. 查找索引位置
            try (RandomAccessFile indexFile = new RandomAccessFile(indexPath, "rw")) {
                long fileLength = indexFile.length();
                long pos = 0;

                while (pos + INDEX_ENTRY_LENGTH <= fileLength) {
                    indexFile.seek(pos);
                    String entryId = readIdFromIndex(indexFile);

                    if (entryId.equals(id)) {
                        // 2. 标记删除（写入空ID）
                        indexFile.seek(pos);
                        indexFile.write(new byte[16]); // 用空字节覆盖UUID
                        return true;
                    }
                    pos += INDEX_ENTRY_LENGTH;
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 整理数据文件（清除已删除数据，减少碎片）
     * @param basePath 基础路径
     */

    public static void defrag(String basePath) throws Exception {
        lock.writeLock().lock();
        try {
            String oldDataPath = basePath + DATA_EXTENSION;
            String oldIndexPath = basePath + INDEX_EXTENSION;
            String newDataPath = basePath + ".defrag" + DATA_EXTENSION;
            String newIndexPath = basePath + ".defrag" + INDEX_EXTENSION;

            try (RandomAccessFile oldIndexFile = new RandomAccessFile(oldIndexPath, "r");
                 RandomAccessFile oldDataFile = new RandomAccessFile(oldDataPath, "r");
                 RandomAccessFile newDataFile = new RandomAccessFile(newDataPath, "rw");
                 RandomAccessFile newIndexFile = new RandomAccessFile(newIndexPath, "rw")) {

                long fileLength = oldIndexFile.length();
                long pos = 0;
                while (pos + INDEX_ENTRY_LENGTH <= fileLength) {
                    oldIndexFile.seek(pos);
                    long mostSigBits = oldIndexFile.readLong();
                    long leastSigBits = oldIndexFile.readLong();
                    boolean isDeleted = (mostSigBits == 0 && leastSigBits == 0);

                    byte[] descBytes = new byte[DESC_BYTE_LENGTH];
                    oldIndexFile.readFully(descBytes);
                    String description = new String(descBytes, StandardCharsets.UTF_8).replace("\0", "").trim();

                    long offset = oldIndexFile.readLong();
                    int length = oldIndexFile.readInt();

                    if (!isDeleted) {
                        oldDataFile.seek(offset);
                        byte[] data = new byte[length];
                        oldDataFile.readFully(data);
                        long newOffset = newDataFile.length();
                        newDataFile.seek(newOffset);
                        newDataFile.write(data);
                        // 写入新索引
                        newIndexFile.writeLong(mostSigBits);
                        newIndexFile.writeLong(leastSigBits);
                        newIndexFile.write(descBytes);
                        newIndexFile.writeLong(newOffset);
                        newIndexFile.writeInt(length);
                    }
                    pos += INDEX_ENTRY_LENGTH;
                }
            }
            // 替换旧文件
            Path oldDatepath = Paths.get(oldDataPath);
            Files.delete(oldDatepath);
            Path oldIndexpath = Paths.get(oldIndexPath);
            Files.delete(oldIndexpath);
            Files.move(Paths.get(newDataPath), oldDatepath);
            Files.move(Paths.get(newIndexPath), oldIndexpath);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // 查找索引条目
    private static IndexEntry findIndexEntry(String id, String indexPath) throws Exception {
        try (RandomAccessFile indexFile = new RandomAccessFile(indexPath, "r")) {
            long fileLength = indexFile.length();
            long pos = 0;

            while (pos + INDEX_ENTRY_LENGTH <= fileLength) {
                indexFile.seek(pos);
                String entryId = readIdFromIndex(indexFile);

                byte[] descBytes = new byte[DESC_BYTE_LENGTH];
                indexFile.readFully(descBytes);
                String description = new String(descBytes, StandardCharsets.UTF_8).replace("\0", "").trim();

                long offset = indexFile.readLong();
                int length = indexFile.readInt();

                if (entryId.equals(id)) {
                    return new IndexEntry(entryId, offset, length, description);
                }
                pos += INDEX_ENTRY_LENGTH;
            }
        }
        return null;
    }


    // 从索引中读取ID
    private static String readIdFromIndex(RandomAccessFile indexFile) throws IOException {
        long mostSigBits = indexFile.readLong();
        long leastSigBits = indexFile.readLong();
        return new UUID(mostSigBits, leastSigBits).toString();
    }

    // 写入索引条目
    private static void writeIndexEntry(RandomAccessFile indexFile, String id, long offset, int length, String description) throws IOException {
        // 写入UUID
        UUID uuid = UUID.fromString(id);
        indexFile.writeLong(uuid.getMostSignificantBits());
        indexFile.writeLong(uuid.getLeastSignificantBits());
        // 处理描述：定长20字，UTF-8编码，补零
        byte[] descBytes = description.getBytes(StandardCharsets.UTF_8);
        byte[] descFixed = new byte[DESC_BYTE_LENGTH];
        int copyLen = Math.min(descBytes.length, DESC_BYTE_LENGTH);
        System.arraycopy(descBytes, 0, descFixed, 0, copyLen);
        indexFile.write(descFixed);
        // 写入偏移量和长度
        indexFile.writeLong(offset);
        indexFile.writeInt(length);
    }


    // 序列化
    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        }
    }

    // 反序列化
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }

    // AES加密
    private static byte[] encrypt(byte[] data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY_BYTES, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // AES解密
    private static byte[] decrypt(byte[] encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY_BYTES, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    // 计算哈希
    private static byte[] calculateHashBytes(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return digest.digest(data);
    }

    public static List<IndexEntry> listAllIdDescriptions(String basePath) throws IOException {
        List<IndexEntry> result = new ArrayList<>();
        String indexPath = basePath + INDEX_EXTENSION;
        try (RandomAccessFile indexFile = new RandomAccessFile(indexPath, "r")) {
            long fileLength = indexFile.length();
            long pos = 0;
            while (pos + INDEX_ENTRY_LENGTH <= fileLength) {
                indexFile.seek(pos);
                long mostSigBits = indexFile.readLong();
                long leastSigBits = indexFile.readLong();
                boolean isDeleted = (mostSigBits == 0 && leastSigBits == 0);
                byte[] descBytes = new byte[DESC_BYTE_LENGTH];
                indexFile.readFully(descBytes);
                String description = new String(descBytes, StandardCharsets.UTF_8).replace("\0", "").trim();
                long offset = indexFile.readLong();
                int length = indexFile.readInt();
                if (!isDeleted) {
                    String id = new UUID(mostSigBits, leastSigBits).toString();
                    result.add(new IndexEntry(id, offset, length, description));
                }
                pos += INDEX_ENTRY_LENGTH;
            }
        }
        return result;
    }



    // 索引条目内部类
    private static class IndexEntry {
        String id;
        long dataOffset;
        int dataLength;
        String description;

        IndexEntry(String id, long dataOffset, int dataLength, String description) {
            this.id = id;
            this.dataOffset = dataOffset;
            this.dataLength = dataLength;
            this.description = description;
        }
    }


    public static void main(String[] args) {
        String savePath = "./src/main/resources/data/effects";
        AbstractNonEntities fire = new AbstractPersistentEffect("火焰", "持续燃烧效果", 3, ElementType.FIRE) {
            @Override
            public boolean isAddedSuccessfully(BaseEntity target) {
                return true;
            }

            private int currentStack = 5;
            @Override
            public void increaseLevel() {
                System.out.println("火焰效果提升一级！");
                currentStack += 1;
            }

            @Override
            public void decreaseLevel() {
                System.out.println("火焰效果降低一级！");
                currentStack = Math.max(5, currentStack - 1);
            }

            @Override
            public boolean trigger(BaseEntity target) {
                if (target instanceof LifeTrait && super.isActive()) {
                    double damage = (this.currentStack * this.getStackCount()) / 100.0;
                    ((LifeTrait) target).damage((int) (damage * ((LifeTrait) target).getConstitution() * 10));
                    return true;
                }
                return false;
            }
        };
        try {
//            String effectId = BinarySecureSaveUtil.add(fire, savePath);
            fire.setLevel(5);
            String effectId = "95db9a0b-dd75-4d6d-beb8-5af5f95031bc";
            BinarySecureSaveUtil.update(effectId, fire, savePath);
            var ids = BinarySecureSaveUtil.listAllIdDescriptions(savePath);
            System.out.println("当前存档中的所有ID：");
            for (IndexEntry id : ids) {
                System.out.println(id.description + " -> " + id.id);
                var effect = (AbstractPersistentEffect) BinarySecureSaveUtil.get(id.id, savePath);
                if (effect != null) {
                    System.out.println(effect.showEffectInfo());
                    System.out.println(effect.getId());
                }
                // 删除对象
//                BinarySecureSaveUtil.delete(id.id,savePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
