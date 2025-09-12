package io.github.Nothingness_is_everywhere;

import io.github.Nothingness_is_everywhere.entity.life.Player;
import io.github.Nothingness_is_everywhere.entity.nonEntities.FireBuff;
import io.github.Nothingness_is_everywhere.entity.item.ItemTrait;
import io.github.Nothingness_is_everywhere.entity.item.consumable.HealingPotion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Player player = new Player("Hero", "The main character", 0, 0, 0);
        player.damage(20);
        System.out.println("Player Health: " + player.getHealth());
        HealingPotion potion = new HealingPotion(30);
        // 2. 创建火焰buff实例（持续3回合，每回合基础伤害5点）
        FireBuff fireBuff = new FireBuff(0, 10);
        fireBuff.setName("1级引燃");

        // 3. 激活buff（作用到玩家身上）
        player.addEffect(fireBuff);

        Map<ItemTrait, Integer> consumables = new HashMap<>();
        consumables.put(potion, 1);
        // 4. 模拟游戏循环（每回合调用tick()，触发持续伤害）
        for (int i = 1; i <= 4; i++) { // 循环4次，观察buff从生效到失效的过程
            System.out.println("\n===== 第" + i + "回合 =====");
            player.activateAllEffects();
            System.out.println(player.getName() + "受到" + fireBuff.getName() + "点火焰伤害");
            System.out.println("当前生命值：" + player.getHealth());
            System.out.println("剩余时间：" + fireBuff.getDuration() + "回合");
            player.useConsumables(consumables);
            System.out.println("玩家当前状态：" + player.getHealth());

        }
        player.moveTo(1,2,0);
        System.out.println(player.getName());
        System.out.println(Arrays.toString(player.getPosition()));
    }
}