package io.github.Nothingness_is_everywhere.entity.item.consumable;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.item.ItemTrait;
import io.github.Nothingness_is_everywhere.entity.item.ItemType;
import io.github.Nothingness_is_everywhere.entity.life.AbstractLife;

/**
 * 治疗药水（消耗品类物品）
 */
public class HealingPotion extends BaseEntity implements ItemTrait {
    private int healAmount; // 基础治疗量

    public HealingPotion(int healAmount) {
        super("治疗药水", "恢复生命值的红色药剂", 0, 0, 0);
        this.healAmount = Math.max(1, healAmount); // 确保治疗量为正数
    }

    // 使用效果：立即恢复生命值 + 附加持续恢复buff
    @Override
    public void use(AbstractLife target) {
        if (target.isAlive()) {
            // 立即治疗
            target.setIsInformation(false);
            target.heal(healAmount);
            System.out.printf("%s通过使用了治疗药水恢复了%d点生命值，剩余生命值：%d%n",
                    target.getName(), healAmount, target.getHealth());
            target.setIsInformation(true);
            // 附加持续恢复buff（3回合，每回合恢复5点）
            System.out.printf("%s使用了%s，额外获得3回合持续恢复效果%n", target.getName(), getName());

        } else {
            System.out.println("目标已死亡，无法使用治疗药水");
        }
    }

    @Override
    public boolean isStackable() {
        return true; // 治疗药水可堆叠
    }

    @Override
    public int getWeight() {
        return 5; // 重量：5克/瓶
    }

    @Override
    public ItemType getType() {
        return ItemType.CONSUMABLE;
    }

    @Override
    public String showInfo() {
        return String.format("【%s】：%s，恢复%d点生命，重量%dg",
                getName(), getDescription(), healAmount, getWeight());
    }
}