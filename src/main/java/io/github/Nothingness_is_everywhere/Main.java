package io.github.Nothingness_is_everywhere;

import io.github.Nothingness_is_everywhere.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        AbstractLife player = new AbstractLife("Hero","The brave adventurer", 100) {
            @Override
            public String showInfo() {
                return "";
            }
        };
        player.damage(20);
        System.out.println("Player Health: " + player.getHealth());
        HealingPotion potion = new HealingPotion(30);
        // 2. 创建火焰buff实例（持续3回合，每回合基础伤害5点）
        FireBuff fireBuff = new FireBuff(0, 10);
        fireBuff.setName("1级引燃");

        // 3. 激活buff（作用到玩家身上）
        fireBuff.activate(player);
        List<AbstractNonLiving> effects = new ArrayList<>();
        effects.add(fireBuff);
        player.setEffects(effects);

        Map<ItemTrait, Integer> consumables = new HashMap<>();
        consumables.put(potion, 1);
        consumables.put(new HealingPotion(-20), 2);
        // 4. 模拟游戏循环（每回合调用tick()，触发持续伤害）
        for (int i = 1; i <= 4; i++) { // 循环4次，观察buff从生效到失效的过程
            System.out.println("\n===== 第" + i + "回合 =====");
            for (AbstractNonLiving j : player.getEffects()) {
                boolean isActive = j.tick(player); // 每回合更新buff状态
                j.showEffectInfo();
                System.out.println("剩余时间：" + fireBuff.getDuration() + "回合");
                System.out.println("buff状态：" + (isActive ? "生效中" : "已失效"));
                player.useConsumables(consumables);
                System.out.println("玩家当前状态：" + player.getHealth());
                System.out.println(player.getId());
            }
        }
        player.moveTo(1,2,0);
        System.out.println(player.getName());
        System.out.println(player.getPosition());
    }
}