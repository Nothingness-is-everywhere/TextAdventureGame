package io.github.Nothingness_is_everywhere.entity.nonEntities.persistent;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.nonEntities.AbstractNonEntities;

public abstract class AbstractPersistentEffect extends AbstractNonEntities {
    protected int duration;              // 持续时间（-1=永久，0=即时失效）
    protected int stackCount;            // 叠加层数
    protected int cooldown;              // 冷却时间（每步递减）

    public AbstractPersistentEffect(String name, String description, int duration, int cooldown) {
        super(name, description);
        this.duration = duration;
        this.stackCount = 1;             // 初始层数1
        this.cooldown = cooldown;
    }

    // 每步更新（核心逻辑：处理持续时间、冷却，返回是否有效）
    public boolean tick(BaseEntity target) {
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

    // 叠加/减层逻辑（复用原逻辑）
    public boolean stack(int maxStack) {
        if (stackCount < maxStack) {
            stackCount++;
            return true;
        }
        return false;
    }

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

    // 持续型特有的Getter/Setter
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public int getStackCount() { return stackCount; }
    public int getCooldown() { return cooldown; }
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }
}
