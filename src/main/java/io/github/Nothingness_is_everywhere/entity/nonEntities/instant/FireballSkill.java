package io.github.Nothingness_is_everywhere.entity.nonEntities.instant;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.life.AbstractLife;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;

public class FireballSkill extends AbstractInstantEffectTrait {
    private int damage;  // 即时伤害

    public FireballSkill(int cooldown, int cost, int damage) {
        super("火球术", "释放一个火球造成魔法伤害", cooldown, cost);
        this.damage = damage;
    }

    @Override
    public boolean trigger(BaseEntity target) {
        if (!(target instanceof LifeTrait)) {
            System.out.println("目标无法被火球术攻击");
            return false;
        }
        LifeTrait lifeTarget = (AbstractLife) target;
        lifeTarget.damage(damage);
        System.out.printf("%s释放了%s，对%s造成%d点伤害！%n",
                ((BaseEntity) lifeTarget).getName(), getName(), target.getName(), damage);
        return true;
    }

    @Override
    public boolean hasEnoughResource(LifeTrait caster) {
        // 假设消耗智力值的10%作为魔力（示例逻辑）
        return caster.getIntelligence() * 0.1 >= getCost();
    }

    @Override
    public String showEffectInfo() {
        return String.format("【%s】%s，冷却时间：%d，消耗：%d魔力",
                getName(), getDescription(), getCooldown(), getCost());
    }
}
