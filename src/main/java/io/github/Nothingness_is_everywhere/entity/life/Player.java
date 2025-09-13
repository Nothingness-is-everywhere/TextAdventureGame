package io.github.Nothingness_is_everywhere.entity.life;

import io.github.Nothingness_is_everywhere.entity.item.ItemTrait;
import java.util.HashMap;
import java.util.Map;

/**
 * 玩家类
 * 继承生命实体基础类，添加玩家独有属性（幸运值、物品栏）
 */
public class Player extends AbstractLife {
    private double luck;                 // 幸运值（影响掉落、暴击等概率）
    private final Map<ItemTrait, Integer> inventory; // 物品栏（物品 -> 数量）


    /**
     * 玩家类构造器
     * @param name 玩家名称
     * @param description 玩家描述
     * @param x 玩家的X坐标位置
     * @param y 玩家的Y坐标位置
     * @param z 玩家的Z坐标位置（高度）
     */

    public Player(String name, String description, int x, int y, int z) {
        super(name, description, x, y, z, 10, 5, 5); // 初始属性：体质10，力量5，智力5
        this.luck = 10.0;                  // 初始幸运值10
        this.inventory = new HashMap<>();
    }

    // 玩家独有：幸运值
    public double getLuck() {
        return luck;
    }

    public void setLuck(double luck) {
        this.luck = Math.max(0, Math.min(100, luck)); // 幸运值范围0-100
    }

    // 物品栏操作：添加物品
    public void addToInventory(ItemTrait item, int count) {
        if (count <= 0) return;
        inventory.put(item, inventory.getOrDefault(item, 0) + count);
        System.out.printf("获得【%s】x%d，当前数量：%d%n",
                item.getName(), count, inventory.get(item));
    }

    // 物品栏操作：移除物品
    public boolean removeFromInventory(ItemTrait item, int count) {
        if (!inventory.containsKey(item) || inventory.get(item) < count) {
            return false;
        }
        int newCount = inventory.get(item) - count;
        if (newCount == 0) {
            inventory.remove(item);
        } else {
            inventory.put(item, newCount);
        }
        System.out.printf("消耗【%s】x%d，剩余数量：%d%n",
                item.getName(), count, newCount);
        return true;
    }

    // 物品栏操作：获取物品数量
    public int getInventoryCount(ItemTrait item) {
        return inventory.getOrDefault(item, 0);
    }

    // 展示玩家信息
    @Override
    public String showInfo() {
        return String.format("""
                【玩家：%s】
                生命值：%d/%d
                属性：体质=%d，力量=%d，智力=%d，速度=%d
                防御：物抗=%d，魔抗=%d
                概率：暴击=%.1f%%，防爆=%.1f%%，幸运=%.1f%%
                物品栏：%d种物品
                坐标：%s
                状态：%s""",
                getName(),
                getHealth(), getConstitution() * 10,
                constitution, strength, intelligence, speed,
                physicalDefense, magicDefense,
                critRate, antiCritRate, luck,
                inventory.size(),
                getPosition(),
                getState()
        );
    }
}