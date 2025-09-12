package io.github.Nothingness_is_everywhere.entity.nonEntities;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;

// 恢复Buff
public class HealBuff extends AbstractNonEntities {
    private int healPerTick;  // 每 tick 的恢复量

    public HealBuff(int duration, int healPerTick) {
        super("生命恢复", "每秒恢复一定生命值", duration);
        this.healPerTick = healPerTick;
    }

    @Override
    public void activate(BaseEntity target) {
        setActive(true);
        System.out.printf("%s获得了%s效果！%n", target.getClass().getSimpleName(), getName());
    }

    @Override
    public void deactivate(BaseEntity target) {
        setActive(false);
        System.out.printf("%s的%s效果结束了%n", target.getClass().getSimpleName(), getName());
    }

    // 重写tick方法：每步恢复生命值
    @Override
    public boolean tick(BaseEntity target) {
        if (!(target instanceof LifeTrait)) {
            System.out.println("目标无法恢复生命值");
            return false;
        } else if (super.tick(target)) {  // 先执行父类的持续时间逻辑
            LifeTrait lifeTarget = (LifeTrait) target;
            int totalHeal = healPerTick * getStackCount();  // 恢复量受叠加层数影响
            lifeTarget.heal(totalHeal);  // 假设LifeTrait有heal方法用于恢复生命值
            System.out.printf("%s通过%s恢复了%d点生命值（叠加%d层）%n",
                    target.getClass().getSimpleName(), getName(), totalHeal, getStackCount());
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
