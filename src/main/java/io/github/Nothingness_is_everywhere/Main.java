package io.github.Nothingness_is_everywhere;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.battle.Battle;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;
import io.github.Nothingness_is_everywhere.entity.life.Player;
import io.github.Nothingness_is_everywhere.entity.nonEntities.persistent.AbstractPersistentEffect;
import io.github.Nothingness_is_everywhere.util.BinarySecureSaveUtil;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 初始化战斗单位
        Player player = new Player("英雄", "主角", 0, 0, 0);

        String savePath = "./src/main/resources/data/savegame";
        LifeTrait monster1 = new Player("野狼", "普通怪物", 1, 1, 0); // 假设存在Monster类
        LifeTrait monster2 = new Player("巨狼", "精英怪物", 1, 2, 0);
        AbstractPersistentEffect fire = (AbstractPersistentEffect) BinarySecureSaveUtil.load(savePath);
        if (fire != null) {
            fire.setLevel(8);
        }
        monster1.addEffect(fire);
        player.addEffect(new AbstractPersistentEffect("恢复", "恢复10点伤害", 3) {
            private int currentStack = 10;
            @Override
            public void increaseLevel() {
                System.out.println("恢复效果提升一级！");
                currentStack += 5;
            }

            @Override
            public void decreaseLevel() {
                System.out.println("恢复效果降低一级！");
                currentStack = Math.max(5, currentStack - 5);
            }

            @Override
            public boolean trigger(BaseEntity target) {
                if (target instanceof LifeTrait && super.isActive()) {
                    ((LifeTrait) target).heal(this.currentStack);
                    return true;
                }
                return false;
            }
        });

// 组建队伍
        List<LifeTrait> teamA = Collections.singletonList(player);
        List<LifeTrait> teamB = Arrays.asList(monster1, monster2);
// 开始战斗
        new Battle(teamA, teamB).startBattle();
    }
}