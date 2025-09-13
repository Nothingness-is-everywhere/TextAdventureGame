package io.github.Nothingness_is_everywhere.entity.nonEntities.instant;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;
import io.github.Nothingness_is_everywhere.entity.nonEntities.AbstractNonEntities;

public abstract class AbstractInstantEffect extends AbstractNonEntities {
    protected int cooldown;              // 技能冷却（释放后进入冷却，与持续型冷却逻辑不同）
    protected int cost;                  // 消耗资源（如魔力值/怒气值，根据游戏设定）

    public AbstractInstantEffect(String name, String description, int cooldown, int cost) {
        super(name, description);
        this.cooldown = cooldown;
        this.cost = cost;
    }

    // 核心方法：即时触发效果（替代持续型的tick）
    public abstract boolean trigger(BaseEntity target);

    // 冷却更新（每回合递减，与持续型逻辑分离）
    public void updateCooldown() {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    // 检查是否可释放（冷却结束且资源足够）
    public boolean canTrigger(LifeTrait caster) {
        return cooldown == 0 && hasEnoughResource(caster);
    }

    // 检查资源是否足够（抽象方法，子类实现具体逻辑）
    protected abstract boolean hasEnoughResource(LifeTrait caster);

    // 非持续型特有的Getter/Setter
    public int getCooldown() { return cooldown; }
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }
    public int getCost() { return cost; }
}
