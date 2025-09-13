package io.github.Nothingness_is_everywhere.entity.nonEntities;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.life.AbstractLife;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;

// 火焰
public class FireBuff extends AbstractNonEntities {
    private int damagePerTick;  // 每 tick 的伤害

    public FireBuff(int duration, int damagePerTick) {
        super("火焰灼烧（叠加1层）", "每回合造成持续伤害", duration,0);
        this.damagePerTick = damagePerTick;
    }

    // 重写tick方法：每步造成伤害
    @Override
    public boolean tick(BaseEntity target) {
        if (! (target instanceof LifeTrait)) {
            System.out.println("目标无法受到火焰伤害");
            return false;
        }else if (super.tick(target)) {  // 先执行父类的持续时间逻辑
            LifeTrait lifeTarget = (AbstractLife) target;
            int totalDamage = damagePerTick * getStackCount();  // 伤害受叠加层数影响
            lifeTarget.setIsInformation(false);
            lifeTarget.damage(totalDamage);
            System.out.printf("%s受到%s%d点伤害（叠加%d层），剩余生命值：%d%n",
                    target.getName(), getName(), totalDamage, getStackCount(), lifeTarget.getHealth());
            lifeTarget.setIsInformation(true);
            return true;
        }
        return false;
    }

    @Override
    public String showEffectInfo() {
        return String.format("【%s】%s，剩余时间：%d，当前层数：%d",
                getName(), getDescription(), getDuration(), getStackCount());
    }
}
