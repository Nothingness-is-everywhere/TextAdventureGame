package io.github.Nothingness_is_everywhere;

import io.github.Nothingness_is_everywhere.entity.life.Player;
import io.github.Nothingness_is_everywhere.entity.nonEntities.FireBuff;
import io.github.Nothingness_is_everywhere.entity.item.ItemTrait;
import io.github.Nothingness_is_everywhere.entity.item.consumable.HealingPotion;
import io.github.Nothingness_is_everywhere.entity.nonEntities.HealBuff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Player player = new Player("Hero", "The main character", 0, 0, 0);
        player.damage(20);
        System.out.println("Player Health: " + player.getHealth());
        HealingPotion potion = new HealingPotion(30);
        // 3. 激活buff（作用到玩家身上）
        player.addEffect(new FireBuff(3, 10)); // 3回合，每回合10点火焰伤害
        player.addEffect(new HealBuff(3, 5));  // 3回合，每回合5点治疗
        player.addEffect(new FireBuff(4, 35));  // 2回合，每回合5点火焰伤害，测试叠加效果
        player.addEffect(new FireBuff(0,10));
        Map<ItemTrait, Integer> consumables = new HashMap<>();
        consumables.put(potion, 1);
        // 4. 模拟游戏循环（每回合调用tick()，触发持续伤害）
        for (int i = 1; i <= 4; i++) { // 循环4次，观察buff从生效到失效的过程
            System.out.println("\n===== 第" + i + "回合 =====");
            player.activateAllEffects();
            player.useConsumables(consumables);
            player.getActiveEffects();
        }
        player.moveTo(1,2,0);
        System.out.println(player.getName());
        System.out.println(Arrays.toString(player.getPosition()));
    }
}