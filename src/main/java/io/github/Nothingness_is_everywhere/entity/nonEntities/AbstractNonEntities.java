package io.github.Nothingness_is_everywhere.entity.nonEntities;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;

import java.util.UUID;

/**
 * 非实体的基础类（如buff、技能、天赋等）
 * 核心特征：具有效果、持续规则、激活状态，但无物理形态和生命特征
 */
public abstract class AbstractNonEntities {
    private final String id;              // 唯一标识（类似“效果ID”）
    private String name;                  // 名称（如“火焰buff”“火球术”）
    private String description;           // 效果描述（如“每秒造成5点伤害”）
    private int duration;                 // 持续时间（-1表示永久，0表示即时生效）
    private boolean isActive;             // 是否激活（未激活的效果不生效）
    private int stackCount;               // 叠加层数（适用于可叠加的效果）

    /**
     * 构造器：初始化非生命实体的基础属性
     * @param name 名称
     * @param description 效果描述
     * @param duration 持续时间（-1=永久，0=即时）
     */
    public AbstractNonEntities(String name, String description, int duration) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.isActive = false;  // 初始未激活
        this.stackCount = 1;    // 初始叠加层数为1
    }

    /**
     * 激活效果（核心方法：使效果开始作用）
     * @param target 作用目标（通常是生命实体，如Player/NPC）
     */
    public abstract void activate(BaseEntity target);

    /**
     * 失效效果（核心方法：使效果停止作用）
     * @param target 作用目标
     */
    public abstract void deactivate(BaseEntity target);

    /**
     * 每步更新（用于处理持续效果，如buff每回合减益）
     * @param target 作用目标
     * @return 是否仍有效（false表示已结束）
     */
    public boolean tick(BaseEntity target) {
        if (!isActive) return false;

        // 处理持续时间（永久效果不减少持续时间）
        if (duration > 0) {
            duration--;
            if (duration <= 0) {
                deactivate(target);  // 持续时间结束，自动失效
                return false;
            }
        }
        return true;
    }

    /**
     * 叠加效果（适用于可叠加的buff/技能）
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
     * 减少叠加层数（掉层）
     * @param minStack 最小层数（通常为1，小于等于此值时会直接失效）
     * @return 是否掉层成功（若已达最小层数则返回false）
     */
    public boolean decreaseStack(int minStack) {
        // 若当前层数大于最小层数，则减少一层
        if (stackCount > minStack) {
            stackCount--;
            return true;
        }
        // 若已达最小层数，掉层后直接失效
        else if (stackCount == minStack) {
            stackCount--;
            deactivate(null);  // 层数归零，效果失效（目标可根据实际需求传入）
            return false;
        }
        return false;
    }

    // Getter/Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public boolean getActive() { return isActive; }
    protected void setActive(boolean active) { isActive = active; }
    public int getStackCount() { return stackCount; }

    /**
     * 展示效果信息（包含状态、持续时间、叠加层数）
     */
    public abstract String showEffectInfo();
}
