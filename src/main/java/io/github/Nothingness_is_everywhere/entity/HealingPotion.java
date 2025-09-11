package io.github.Nothingness_is_everywhere.entity;

import java.util.List;

/**
 * 治疗药水（具体物品分子）
 * 在原子基础上，实现物品特性，定义“治疗”功能
 */
public class HealingPotion extends BaseEntity implements ItemTrait {
    private int healAmount; // 治疗量（物品的核心属性）

    public HealingPotion(int healAmount) {
        super("治疗药水", "一瓶能恢复生命的红色药剂",0,0,0); // 继承原子基础属性
        this.healAmount = healAmount;
    }

    // 物品核心功能：给目标恢复生命（分子与生命的交互）
    @Override
    public void use(AbstractLife target) {
        if (target.isAlive()) {
            if (target.getEffects()!= null) {
                List<AbstractNonLiving> effects = new List<AbstractNonLiving>;
                effects.add(new PoisonEffect());
            }
            target.heal(healAmount);
            System.out.printf("使用了%s，%s恢复了%d点生命！%n",
                    getName(), target.getClass().getSimpleName(), healAmount);
        } else {
            System.out.println("目标已死亡，无法使用！");
        }
    }

    // 物品特性：不可堆叠（分子结构不可叠加）
    @Override
    public boolean isStackable() {
        return false;
    }

    // 物品重量（分子物理属性）
    @Override
    public int getWeight() {
        return 5; // 单位：克
    }

    // 展示物品信息（分子属性展示）
    @Override
    public String showInfo() {
        return String.format("【%s】：%s，恢复%d点生命，重量%dg",
                getName(), getDescription(), healAmount, getWeight());
    }

    @Override
    public ItemType getType() {
        return ItemType.CONSUMABLE;
    }
}
