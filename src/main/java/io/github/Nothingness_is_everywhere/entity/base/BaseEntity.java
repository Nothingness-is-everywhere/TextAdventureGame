package io.github.Nothingness_is_everywhere.entity.base;

import io.github.Nothingness_is_everywhere.entity.nonEntities.AbstractNonEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 所有实体的最基础原子类
 * 包含核心属性、空间坐标和生命周期管理
 */
public abstract class BaseEntity {
    // 实体状态枚举
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

    public BaseEntity(String name, String description, int x, int y, int z) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.x = x;
        this.y = y;
        this.z = z;
        this.state = EntityState.ACTIVE;    // 初始为活跃状态
    }

    // 坐标移动
    public void moveTo(int moveX, int moveY, int moveZ) {
        if (state != EntityState.DESTROYED) {
            this.x += moveX;
            this.y += moveY;
            this.z += moveZ;
            System.out.printf("%s移动到了坐标(%d, %d, %d)%n", getName(), this.x, this.y, this.z);
        }
    }

    // 状态管理：标记为销毁（触发回收前清理）
    public void destroy() {
        if (state != EntityState.DESTROYED) {
            state = EntityState.DESTROYED;
        }
    }

    // Getter/Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int[] getPosition() { return new int[]{x, y, z}; }
    public EntityState getState() { return state; }
    public void setState(EntityState state) { this.state = state; }

    // 抽象方法：展示实体信息
    public abstract String showInfo();
}