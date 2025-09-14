package io.github.Nothingness_is_everywhere.entity.battle;

import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;
import io.github.Nothingness_is_everywhere.util.ProbabilityUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 战斗管理器类
 * 处理多对多战斗流程，支持偷袭回合（第0回合）和速度优先的回合制战斗
 */
public class Battle {
    private final List<LifeTrait> teamA; // 队伍A
    private final List<LifeTrait> teamB; // 队伍B
    private int currentRound; // 当前回合数
    private boolean isBattleEnded; // 战斗是否结束

    public Battle(List<LifeTrait> teamA, List<LifeTrait> teamB) {
        this.teamA = new ArrayList<>(teamA);
        this.teamB = new ArrayList<>(teamB);
        this.currentRound = 0;
        this.isBattleEnded = false;
    }

    /**
     * 开始战斗流程
     */
    public void startBattle() {
        System.out.println("===== 战斗开始 =====");
        // 显示参战双方
        System.out.println("队伍A参战成员：" + getTeamNames(teamA));
        System.out.println("队伍B参战成员：" + getTeamNames(teamB));

        // 第0回合：偷袭回合（仅特殊非实体效果触发）
        processAmbushRound();
        if (isBattleEnded) {
            endBattle();
            return;
        }

        // 普通回合战斗循环
        while (!isBattleEnded) {
            currentRound++;
            System.out.printf("%n===== 第%d回合 =====", currentRound);
            processNormalRound();
            if (checkBattleEnd()) {
                endBattle();
                break;
            }
        }
    }

    /**
     * 处理第0回合（偷袭回合）
     * 仅触发带有偷袭标签的非实体效果（如伏击、先手buff等）
     */
    private void processAmbushRound() {
        System.out.println("\n===== 第0回合（偷袭） =====");
        // 触发所有实体的偷袭效果（假设效果名称含"偷袭"关键词，实际可通过接口标记）
        List<LifeTrait> allEntities = new ArrayList<>();
        allEntities.addAll(teamA);
        allEntities.addAll(teamB);

//        for (LifeTrait entity : allEntities) {
//            for (AbstractNonEntities effect : new ArrayList<>(entity.getActiveEffects())) {
//                if (effect.getName().contains("偷袭") && effect.getCooldown() <= 0) {
//                    System.out.printf("[偷袭触发] %s的%s生效！%n",
//                            ((io.github.Nothingness_is_everywhere.entity.base.BaseEntity) entity).getName(),
//                            effect.getName());
//                    effect.tick(entity); // 触发偷袭效果
//                }
//            }
//        }
    }

    /**
     * 处理普通回合（第1回合及以后）
     * 按速度排序所有存活实体，依次执行行动
     */
    private void processNormalRound() {
        // 触发所有持续效果（每回合结束时结算）
        triggerPersistentEffects();
        // 筛选存活实体并按速度排序（速度高的先出手）
        List<LifeTrait> aliveEntities = getAllAliveEntities()
                .stream()
                .sorted(Comparator.comparingInt(LifeTrait::getSpeed).reversed())
                .collect(Collectors.toList());

        // 输出回合出手顺序
        System.out.println("\n出手顺序：" + getEntityNames(aliveEntities));

        // 每个实体依次执行行动
        for (LifeTrait attacker : aliveEntities) {
            if (!attacker.isAlive()) continue; // 防止中途死亡的实体行动

            // 确定攻击目标（对方队伍的存活实体）
            List<LifeTrait> enemies = getEnemies(attacker);
            if (enemies.isEmpty()) continue;

            LifeTrait target = selectTarget(enemies);
            if (target == null) continue;

            // 执行基础攻击
            executeAttack(attacker, target);
        }
    }

    /**
     * 执行攻击逻辑
     */
    private void executeAttack(LifeTrait attacker, LifeTrait target) {
        String attackerName = ((io.github.Nothingness_is_everywhere.entity.base.BaseEntity) attacker).getName();
        String targetName = ((io.github.Nothingness_is_everywhere.entity.base.BaseEntity) target).getName();

        // 基础伤害计算（力量影响物攻）
        int baseDamage = attacker.getStrength() * 2;
        // 防御减免（物抗抵消部分伤害）
        int finalDamage = Math.max(1, baseDamage - target.getPhysicalDefense() / 2);

        // 暴击判定（使用概率工具类）
        boolean isCrit = ProbabilityUtils.isTriggered(attacker.getCritRate());
        if (isCrit) {
            finalDamage = (int) (finalDamage * 1.5); // 暴击伤害1.5倍
            System.out.print("[暴击！] ");
        }

        // 造成伤害
        target.damage(finalDamage);
        System.out.printf("%s攻击了%s，造成%d点伤害！%n", attackerName, targetName, finalDamage);
    }

    /**
     * 触发所有存活实体的持续效果
     */
    private void triggerPersistentEffects() {
        System.out.println("\n\n===== 持续效果触发 =====");
        getAllAliveEntities().forEach(entity -> {
            ((io.github.Nothingness_is_everywhere.entity.life.AbstractLife) entity).activateAllEffects();
        });
    }

    /**
     * 选择攻击目标（随机选择敌方存活实体）
     */
    private LifeTrait selectTarget(List<LifeTrait> enemies) {
        return enemies.get(new Random().nextInt(enemies.size()));
    }

    /**
     * 获取指定实体的敌方队伍
     */
    private List<LifeTrait> getEnemies(LifeTrait entity) {
        if (teamA.contains(entity)) {
            return teamB.stream().filter(LifeTrait::isAlive).collect(Collectors.toList());
        } else {
            return teamA.stream().filter(LifeTrait::isAlive).collect(Collectors.toList());
        }
    }

    /**
     * 检查战斗是否结束（某一方全灭）
     */
    private boolean checkBattleEnd() {
        boolean teamAAlive = teamA.stream().anyMatch(LifeTrait::isAlive);
        boolean teamBAlive = teamB.stream().anyMatch(LifeTrait::isAlive);
        isBattleEnded = !teamAAlive || !teamBAlive;
        return isBattleEnded;
    }

    /**
     * 结束战斗并输出结果
     */
    private void endBattle() {
        System.out.println("\n===== 战斗结束 =====");
        boolean teamAWin = teamA.stream().anyMatch(LifeTrait::isAlive);
        System.out.println(teamAWin ? "队伍A获胜！" : "队伍B获胜！");
        System.out.println("存活成员：");
        System.out.println("队伍A：" + getTeamNames(teamA.stream().filter(LifeTrait::isAlive).collect(Collectors.toList())));
        System.out.println("队伍B：" + getTeamNames(teamB.stream().filter(LifeTrait::isAlive).collect(Collectors.toList())));
    }

    // 工具方法：获取队伍成员名称
    private String getTeamNames(List<LifeTrait> team) {
        return team.stream()
                .map(e -> ((io.github.Nothingness_is_everywhere.entity.base.BaseEntity) e).getName())
                .collect(Collectors.joining("、", "[", "]"));
    }

    // 工具方法：获取实体名称列表
    private String getEntityNames(List<LifeTrait> entities) {
        return entities.stream()
                .map(e -> ((io.github.Nothingness_is_everywhere.entity.base.BaseEntity) e).getName())
                .collect(Collectors.joining(" -> "));
    }

    // 工具方法：获取所有存活实体
    private List<LifeTrait> getAllAliveEntities() {
        List<LifeTrait> alive = new ArrayList<>();
        alive.addAll(teamA.stream().filter(LifeTrait::isAlive).toList());
        alive.addAll(teamB.stream().filter(LifeTrait::isAlive).toList());
        return alive;
    }
}