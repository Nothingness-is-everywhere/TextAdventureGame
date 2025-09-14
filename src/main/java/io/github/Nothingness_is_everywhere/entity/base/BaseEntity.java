package io.github.Nothingness_is_everywhere.entity.base;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * 所有实体的最基础原子类
 * <p>
 * 负责管理实体的核心属性（唯一标识、名称、描述）、空间坐标（x, y, z）以及生命周期（状态管理）。
 * 提供坐标移动、销毁标记等基础操作，供所有游戏实体继承扩展。
 */
public abstract class BaseEntity implements Serializable {
    /**
     * 实体状态枚举
     * ACTIVE：活跃状态
     * INACTIVE：非活跃（暂不可用）
     * DESTROYED：已销毁（待回收）
     */
    public enum EntityState {
        ACTIVE,      // 活跃状态
        INACTIVE,    // 非活跃（暂不可用）
        DESTROYED    // 已销毁（待回收）
    }

    private final String id;                // 唯一标识（原子编号）
    private String name;                    // 实体名称
    private String description;             // 实体描述
    private int x, y, z;                    // 空间坐标
    private EntityState state;              // 实体状态（用于对象回收）
    @Serial
    private static final long serialVersionUID = 1L; // 序列化版本号
    private ElementType elementType;    // 元素属性

    /**
     * 构造器：初始化实体的基础属性和空间坐标(一般应用于构造普通实体)
     * @param name 实体名称
     * @param description 实体描述
     * @param x 初始X坐标
     * @param y 初始Y坐标
     * @param z 初始Z坐标
     */
    public BaseEntity(String name, String description, int x, int y, int z) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.x = x;
        this.y = y;
        this.z = z;
        this.state = EntityState.ACTIVE;    // 初始为活跃状态
        this.elementType = ElementType.NONE; // 默认无属性
    }

    /**
     * 构造器：初始化实体的基础属性，坐标默认为(0,0,0)(一般应用于详细构造高级装备)
     * @param name 实体名称
     * @param description 实体描述
     * @param elementType 元素属性
     */
    public BaseEntity(String name, String description, ElementType elementType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.state = EntityState.ACTIVE;    // 初始为活跃状态
        this.elementType = elementType; // 设置元素属性
    }

    /**
     * 构造器：初始化实体的基础属性、空间坐标和元素属性(一般应用于详细构造高级生命体)
     * @param name 实体名称
     * @param description 实体描述
     * @param x 初始X坐标
     * @param y 初始Y坐标
     * @param z 初始Z坐标
     * @param elementType 元素属性
     */
    public BaseEntity(String name, String description, int x, int y, int z, ElementType elementType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.x = x;
        this.y = y;
        this.z = z;
        this.state = EntityState.ACTIVE;    // 初始为活跃状态
        this.elementType = elementType; // 设置元素属性
    }
    /**
     * 坐标移动
     * @param moveX X方向移动量
     * @param moveY Y方向移动量
     * @param moveZ Z方向移动量
     */
    public void moveTo(int moveX, int moveY, int moveZ) {
        if (state != EntityState.DESTROYED) {
            this.x += moveX;
            this.y += moveY;
            this.z += moveZ;
            System.out.printf("%s移动到了坐标(%d, %d, %d)%n", getName(), this.x, this.y, this.z);
        }
    }

    /**
     * 状态管理：标记为销毁（触发回收前清理）
     */
    public void destroy() {
        if (state != EntityState.DESTROYED) {
            state = EntityState.DESTROYED;
        }
    }

    // Getter/Setter

    /**
     * 获取唯一标识
     * @return id 唯一标识
     */
    public String getId() { return id; }

    /**
     * 获取实体名称
     * @return name 实体名称
     */
    public String getName() { return name; }

    /**
     * 设置实体名称
     * @param name 实体名称
     */
    public void setName(String name) { this.name = name; }

    /**
     * 获取实体描述
     * @return description 实体描述
     */
    public String getDescription() { return description; }

    /**
     * 设置实体描述
     * @param description 实体描述
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * 获取X坐标
     * @return x X坐标
     */
    public int getX() { return x; }

    /**
     * 获取Y坐标
     * @return y Y坐标
     */
    public int getY() { return y; }

    /**
     * 获取Z坐标
     * @return z Z坐标
     */
    public int getZ() { return z; }

    /**
     * 获取空间坐标数组
     * @return int[] 坐标数组{x, y, z}
     */
    public int[] getPosition() { return new int[]{x, y, z}; }

    /**
     * 获取实体状态
     * @return state 实体状态
     */
    public EntityState getState() { return state; }

    /**
     * 设置实体状态
     * @param state 实体状态
     */
    public void setState(EntityState state) { this.state = state; }

    /**
     * 获取元素属性
     * @return elementType 元素属性
     */
    public ElementType getElementType() { return elementType; }

    /**
     * 设置元素属性
     * @param elementType 元素属性
     */
    public void setElementType(ElementType elementType) { this.elementType = elementType; }

    /**
     * 抽象方法：展示实体信息
     * @return 实体信息字符串
     */
    public abstract String showInfo();
}