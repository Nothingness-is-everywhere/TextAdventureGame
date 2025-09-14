package io.github.Nothingness_is_everywhere.entity.nonEntities.persistent;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.base.ElementType;
import io.github.Nothingness_is_everywhere.entity.nonEntities.AbstractNonEntities;

/**
 * 持续型非实体效果抽象类
 * <p>
 * 用于描述游戏中可持续存在的效果（如Buff、Debuff等），
 * 管理持续时间、叠加层数、冷却时间等核心属性。
 * 支持元素属性扩展，便于判定效果类型和交互。
 */
public abstract class AbstractPersistentEffect extends AbstractNonEntities implements PersistentEffectTrait {
    protected int duration;              // 持续时间（-1=永久，0=即时失效）
    protected int stackCount;            // 叠加层数
    protected int cooldown;              // 冷却时间（每步递减）

    /**
     * 构造器：初始化持续效果的基础属性
     * @param name 效果名称
     * @param description 效果描述
     * @param duration 持续时间
     */
    public AbstractPersistentEffect(String name, String description, int duration) {
        super(name, description);
        this.duration = duration;
        this.stackCount = 1;             // 初始层数1
        this.cooldown = 0;
    }

    /**
     * 构造器：初始化持续效果（带元素属性）
     * @param name 效果名称
     * @param description 效果描述
     * @param duration 持续时间
     * @param elementType 元素属性
     */
    public AbstractPersistentEffect(String name, String description, int duration, ElementType elementType) {
        super(name, description, elementType);
        this.duration = duration;
        this.stackCount = 1;
        this.cooldown = 0;
    }

    /**
     * 构造器：初始化持续效果（带冷却时间）
     * @param name 效果名称
     * @param description 效果描述
     * @param duration 持续时间
     * @param cooldown 冷却时间
     */
    public AbstractPersistentEffect(String name, String description, int duration, int cooldown) {
        super(name, description);
        this.duration = duration;
        this.stackCount = 1;
        this.cooldown = cooldown;
    }

    /**
     * 构造器：初始化持续效果（带冷却和元素属性）
     * @param name 效果名称
     * @param description 效果描述
     * @param duration 持续时间
     * @param cooldown 冷却时间
     * @param elementType 元素属性
     */
    public AbstractPersistentEffect(String name, String description, int duration, int cooldown, ElementType elementType) {
        super(name, description, elementType);
        this.duration = duration;
        this.stackCount = 1;
        this.cooldown = cooldown;
    }

    /**
     * 每步更新（处理持续时间和冷却，返回是否有效）
     * @param target 作用目标实体
     * @return 是否仍有效
     */
    public boolean isActive(BaseEntity target) {
        if (cooldown > 0) {
            cooldown--;
            return false; // 冷却中不生效
        }
        if (duration >= 0) {
            duration--;
            return duration >= 0; // 持续时间递减，0时失效
        }
        return true; // 仍有效
    }

    /**
     * 叠加层数（不超过最大层数）
     * @param maxStack 最大叠加层数
     * @return 是否叠加成功
     */
    public boolean stack(int maxStack) {
        if (stackCount < maxStack) {
            stackCount++;
            return true;
        }
        return false;
    }

    /**
     * 减少层数（不低于最小层数）
     * @param minStack 最小层数
     * @return 是否仍有效
     */
    public boolean decreaseStack(int minStack) {
        if (stackCount > minStack) {
            stackCount--;
            return true;
        } else if (stackCount == minStack) {
            stackCount--;
            return false; // 层数为0时失效
        }
        return false;
    }

    // Getter/Setter

    /**
     * 获取持续时间
     * @return 持续时间
     */
    public int getDuration() { return duration; }

    /**
     * 设置持续时间
     * @param duration 持续时间
     */
    public void setDuration(int duration) { this.duration = duration; }

    /**
     * 获取叠加层数
     * @return 叠加层数
     */
    public int getStackCount() { return stackCount; }

    /**
     * 获取冷却时间
     * @return 冷却时间
     */
    public int getCooldown() { return cooldown; }

    /**
     * 设置冷却时间
     * @param cooldown 冷却时间
     */
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }
}