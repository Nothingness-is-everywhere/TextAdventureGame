package io.github.Nothingness_is_everywhere.entity;

/**
 * 生命实体的特性接口（类似“生命基因链片段”）
 * 所有有生命的实体必须实现此接口，表达“生命”相关的核心行为
 */
public interface LifeTrait {
    // 生命值相关（生命的核心特征）
    int getHealth();
    void setHealth(int health);
    void damage(int amount);
    void heal(int amount);

    // 生命状态（存活/死亡）
    boolean isAlive();

    // 感知环境（生命特有的交互能力）
//    String perceive(Scene scene);
}
