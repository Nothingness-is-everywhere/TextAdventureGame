package io.github.Nothingness_is_everywhere.entity;

import java.io.Serializable;

/**
 * 生命实体的基础实现（类似“基础基因链表达”）
 * 继承原子级基础类，实现生命特性接口，封装通用生命逻辑
 */
public abstract class AbstractLife extends BaseEntity implements LifeTrait {
    protected int health; // 生命值（生命特有的属性）
    protected int maxHealth; // 最大生命值（基因决定的上限）

    public AbstractLife(String name, String description, int maxHealth) {
        super(name, description,0,0,0); // 继承“原子”基础属性
        this.maxHealth = maxHealth;
        this.health = maxHealth; // 初始生命值为最大值（基因初始状态）
    }

    // 通用生命逻辑实现（基因的基础表达）
    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth)); // 基因限制：生命值有边界
    }

    @Override
    public void damage(int amount) {
        setHealth(health - amount); // 受伤时生命值减少（基因对伤害的响应）
    }

    @Override
    public void heal(int amount) {
        setHealth(health + amount); // 治疗时生命值增加（基因对恢复的响应）
    }

    @Override
    public boolean isAlive() {
        return health > 0; // 基因定义的“存活”标准
    }

    // 感知环境的基础实现（可被子类重写，体现不同生命的感知差异）
//    @Override
//    public String perceive(Scene scene) {
//        return String.format("%s感知到了%s的环境。", getName(), scene.getId());
//    }
}
