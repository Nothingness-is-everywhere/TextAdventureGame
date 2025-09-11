package io.github.Nothingness_is_everywhere.entity.life;

import io.github.Nothingness_is_everywhere.entity.item.ItemTrait;
import java.util.Map;

/**
 * 生命实体特性接口
 * 定义生命实体的核心行为（生命值、战斗属性、物品使用等）
 */
public interface LifeTrait {
    // 生命值操作
    int getHealth();
    void setHealth(int health);
    void damage(int amount);
    void heal(int amount);
    boolean isAlive();

    // 战斗属性操作
    int getConstitution();    // 体质（影响血量上限）
    int getStrength();        // 力量（影响物攻）
    int getIntelligence();    // 智力（影响魔攻）
    int getSpeed();           // 速度（影响出手顺序）
    int getPhysicalDefense(); // 物抗
    int getMagicDefense();    // 魔抗
    double getCritRate();     // 暴击率（百分比）
    double getAntiCritRate(); // 防爆率（百分比）

    // 物品使用
    void useItem(ItemTrait item);
    void useConsumables(Map<ItemTrait, Integer> consumables);
}