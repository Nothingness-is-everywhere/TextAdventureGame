package io.github.Nothingness_is_everywhere.entity;

import java.util.List;
import java.util.UUID;

/**
 * 所有实体的最基础类（类似“原子”），包含核心属性和空间坐标
 * 生命和物品都继承此类，拥有唯一标识、基础信息和空间位置
 */
public abstract class BaseEntity {
    private final String id;          // 唯一标识（原子编号）
    private String name;              // 实体名称
    private String description;       // 实体描述
    private int x;                    // 空间坐标X（游戏世界中的横向位置）
    private int y;                    // 空间坐标Y（游戏世界中的纵向位置）
    private int z;                    // 空间坐标Z（游戏世界中的高度位置）
    private List<AbstractNonLiving> effects; // 作用于该实体的非生命效果列表（如buff、技能等）

    /**
     * 构造器：初始化实体的基础属性和初始坐标
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
        this.effects = null; // 初始时没有任何效果
    }

    // 坐标操作：移动实体（改变空间位置）
    public void moveTo(int newX, int newY, int newZ) {
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        System.out.printf("%s移动到了坐标(%d, %d, %d)%n", getName(), newX, newY, newZ);
    }

    // 坐标操作：获取当前位置信息
    public String getPosition() {
        return String.format("(%d, %d, %d)", x, y, z);
    }

    // Getter/Setter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public List<AbstractNonLiving> getEffects() {
        return effects;
    }

    public void setEffects(List<AbstractNonLiving> effects) {
        this.effects = effects;
    }

    // 抽象方法：展示实体完整信息（需包含坐标）
    public abstract String showInfo();
}
