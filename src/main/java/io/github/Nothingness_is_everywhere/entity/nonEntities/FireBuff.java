package io.github.Nothingness_is_everywhere.entity.nonEntities;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;

// 火焰
public class FireBuff extends AbstractNonEntities {
    private int damagePerTick;  // 每 tick 的伤害

    public FireBuff(int duration, int damagePerTick) {
        super("火焰灼烧", "每秒造成持续伤害", duration);
        this.damagePerTick = damagePerTick;
    }

    @Override
    public void activate(BaseEntity target) {
        setActive(true);
        System.out.printf("%s被%s覆盖！%n", target.getClass().getSimpleName(), getName());
    }

    @Override
    public void deactivate(BaseEntity target) {
        setActive(false);
        System.out.printf("%s的%s效果消失了%n", target.getClass().getSimpleName(), getName());
    }

    // 重写tick方法：每步造成伤害
    @Override
    public boolean tick(BaseEntity target) {
        if (! (target instanceof LifeTrait)) {
            System.out.println("目标无法受到火焰伤害");
            return false;
        }else if (super.tick(target)) {  // 先执行父类的持续时间逻辑
            LifeTrait lifeTarget = (LifeTrait) target;
            int totalDamage = damagePerTick * getStackCount();  // 伤害受叠加层数影响
            lifeTarget.damage(totalDamage);
            System.out.printf("%s受到%s%d点伤害（叠加%d层）%n",
                    lifeTarget.getClass().getSimpleName(), getName(), totalDamage, getStackCount());
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
