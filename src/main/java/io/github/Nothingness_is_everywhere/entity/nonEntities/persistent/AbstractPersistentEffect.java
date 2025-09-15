package io.github.Nothingness_is_everywhere.entity.nonEntities.persistent;

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
    private int duration;              // 持续时间（-1=永久，0=即时失效）
    private int stackCount;            // 叠加层数
    private int cooldown;              // 冷却时间（每步递减）

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
     * @return 是否仍有效
     */
    public boolean isActive() {
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
     * @return 是否仍有效
     */
    public boolean decreaseStack() {
        if (stackCount > 1) {
            stackCount--;
            return true;
        } else if (stackCount == 1) {
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

    /**
     * 显示效果的详细信息，包括名称、描述、剩余时间、层数、冷却和属性等
     * @return 格式化的效果信息字符串
     */
    @Override
    public String showEffectInfo() {
        String Duration = (getDuration() > 0) ? String.valueOf(getDuration()) : "永久";
        return String.format(
                "【%s】%s\n剩余时间：%s\n当前层数：%d\n冷却时间：%d\n元素属性：%s\n等级：Lv%s",
                getName(),
                getDescription(),
                Duration,
                getStackCount(),
                getCooldown(),
                getElementType().getChineseDesc(),
                getLevel()
        );
    }
}