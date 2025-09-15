package io.github.Nothingness_is_everywhere.util;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.base.ElementType;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;
import io.github.Nothingness_is_everywhere.entity.life.Player;
import io.github.Nothingness_is_everywhere.entity.nonEntities.AbstractNonEntities;
import io.github.Nothingness_is_everywhere.entity.nonEntities.persistent.AbstractPersistentEffect;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

/**
 * 二进制加密存档工具类
 * 特性：所有数据以纯二进制形式写入文件（加密后不做文本编码），体积更小、安全性更高
 */
public class BinarySecureSaveUtil {
    // AES密钥（16字节=128位，必须保持长度合规）
    private static final byte[] SECRET_KEY_BYTES = "GameSaveKey12345".getBytes(); // 直接使用字节数组密钥
    private static final String ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String FILE_EXTENSION = ".dat";

    /**
     * 保存对象到二进制加密DAT文件
     *
     * @param obj      可序列化对象
     * @param filePath 存档路径（不含扩展名）
     */
    public static void save(Object obj, String filePath) {
        if (!(obj instanceof Serializable)) {
            System.err.println("对象未实现Serializable接口，无法保存");
            return;
        }

        try {
            // 1. 序列化对象为二进制字节数组
            byte[] objBytes = serialize(obj);

            // 2. 计算原始数据的SHA-256哈希（二进制字节形式）
            byte[] hashBytes = calculateHashBytes(objBytes);

            // 3. AES加密对象数据（输出二进制加密字节）
            byte[] encryptedBytes = encrypt(objBytes);

            // 4. 写入二进制文件：【哈希长度(4字节int) + 哈希字节 + 加密数据字节】
            String fullPath = filePath + FILE_EXTENSION;
            try (DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(fullPath)))) {
                dos.writeInt(hashBytes.length); // 哈希字节长度（固定4字节二进制int）
                dos.write(hashBytes);           // 哈希二进制字节
                dos.write(encryptedBytes);      // 加密数据二进制字节
            }

            System.out.println("二进制加密存档成功：" + fullPath + "（大小：" + Files.size(Paths.get(fullPath)) + "字节）");

        } catch (Exception e) {
            System.err.println("保存失败：" + e.getMessage());
        }
    }

    /**
     * 从二进制加密DAT文件加载对象
     * @param filePath 存档路径（不含扩展名）
     * @return 解密并校验后的对象
     */
    public static Object load(String filePath) {
        try {
            String fullPath = filePath + FILE_EXTENSION;
            if (!Files.exists(Paths.get(fullPath))) {
                System.err.println("文件不存在：" + fullPath);
                return null;
            }

            // 读取二进制文件内容
            try (DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(fullPath)))) {

                // 1. 读取哈希字节（先读长度，再读内容）
                int hashLength = dis.readInt(); // 读取4字节int作为哈希长度
                byte[] storedHashBytes = new byte[hashLength];
                dis.readFully(storedHashBytes); // 读取哈希二进制字节

                // 2. 读取加密数据字节（剩余所有字节）
                byte[] encryptedBytes = new byte[dis.available()];
                dis.readFully(encryptedBytes);

                // 3. 解密数据（二进制字节解密）
                byte[] decryptedBytes = decrypt(encryptedBytes);

                // 4. 校验哈希（用二进制字节直接比对，无需转字符串）
                byte[] calculatedHashBytes = calculateHashBytes(decryptedBytes);
                if (!MessageDigest.isEqual(storedHashBytes, calculatedHashBytes)) {
                    System.err.println("存档被篡改！哈希校验失败");
                    return null;
                }

                // 5. 反序列化二进制字节为对象
                return deserialize(decryptedBytes);
            }

        } catch (Exception e) {
            System.err.println("加载失败：" + e.getMessage());
            return null;
        }
    }

    // 序列化对象为二进制字节数组
    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray(); // 纯二进制字节
        }
    }

    // 反序列化二进制字节数组为对象
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }

    // AES加密（输入二进制字节，输出加密后的二进制字节）
    private static byte[] encrypt(byte[] data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY_BYTES, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data); // 加密后的二进制字节
    }

    // AES解密（输入加密二进制字节，输出原始二进制字节）
    private static byte[] decrypt(byte[] encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY_BYTES, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData); // 解密后的二进制字节
    }

    // 计算哈希（直接返回二进制字节，不转字符串，减少性能损耗）
    private static byte[] calculateHashBytes(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return digest.digest(data); // 哈希结果为二进制字节
    }

    static class TestObject implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        String name = "TestObject";
        int value = 42;

        @Override
        public String toString() {
            return "Object{name='" + name + "', value=" + value + "}";
        }
    }

    public static void main(String[] args) {
        String savePath = "./src/main/resources/data/savegame";
        AbstractNonEntities fire = new AbstractPersistentEffect("火焰", "持续燃烧效果", 3, ElementType.FIRE) {
            private int currentStack = 10;
            @Override
            public void increaseLevel() {
                System.out.println("火焰效果提升一级！");
                currentStack += 5;
            }

            @Override
            public void decreaseLevel() {
                System.out.println("火焰效果降低一级！");
                currentStack = Math.max(5, currentStack - 5);
            }

            @Override
            public boolean trigger(BaseEntity target) {
                if (target instanceof LifeTrait && super.isActive()) {
                    ((LifeTrait) target).damage(this.currentStack);
                    return true;
                }
                return false;
            }
        };
        BinarySecureSaveUtil.save(fire, savePath);
    }
}
