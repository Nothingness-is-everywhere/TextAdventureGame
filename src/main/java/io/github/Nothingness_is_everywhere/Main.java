package io.github.Nothingness_is_everywhere;

import io.github.Nothingness_is_everywhere.entity.battle.Battle;
import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;
import io.github.Nothingness_is_everywhere.entity.life.Player;
import io.github.Nothingness_is_everywhere.entity.nonEntities.instant.FireballSkill;
import io.github.Nothingness_is_everywhere.entity.nonEntities.persistent.FireBuff;
import io.github.Nothingness_is_everywhere.entity.nonEntities.persistent.HealBuff;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 初始化战斗单位
        Player player = new Player("英雄", "主角", 0, 0, 0);
        player.addEffect(new HealBuff(3,20));

        LifeTrait monster1 = new Player("野狼", "普通怪物", 1, 1, 0); // 假设存在Monster类
        LifeTrait monster2 = new Player("巨狼", "精英怪物", 1, 2, 0);
        monster1.addEffect(new FireBuff(2,40));

// 组建队伍
        List<LifeTrait> teamA = Collections.singletonList(player);
        List<LifeTrait> teamB = Arrays.asList(monster1, monster2);
// 开始战斗
        new Battle(teamA, teamB).startBattle();
    }
}