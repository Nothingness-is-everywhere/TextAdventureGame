package io.github.Nothingness_is_everywhere.entity.life;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.item.ItemTrait;
import io.github.Nothingness_is_everywhere.entity.nonEntities.AbstractNonEntities;

import java.util.List;
import java.util.Map;

/**
 * 生命实体基础类
 * 实现LifeTrait接口，封装生命实体的通用属性和行为
 */
public abstract class AbstractLife extends BaseEntity implements LifeTrait {
    protected int health;             // 当前生命值
    protected int constitution;       // 体质（血量 = 体质 * 10）
    protected int strength;           // 力量（物攻 = 力量 * 2）
    protected int intelligence;       // 智力（魔攻 = 智力 * 2）
    protected int speed;              // 速度
    protected int physicalDefense;    // 物抗
    protected int magicDefense;       // 魔抗
    protected double critRate;        // 暴击率（默认5%）
    protected double antiCritRate;    // 防爆率（默认5%）
    private List<AbstractNonEntities> activeEffects; // 当前激活的效果列表

    public AbstractLife(String name, String description, int x, int y, int z,
                        int constitution, int strength, int intelligence) {
        super(name, description, x, y, z);
        this.constitution = constitution;
        this.strength = strength;
        this.intelligence = intelligence;
        this.health = constitution * 10; // 初始血量 = 体质 * 10
        this.speed = 5;                  // 默认速度
        this.physicalDefense = 0;
        this.magicDefense = 0;
        this.critRate = 5.0;             // 5%基础暴击
        this.antiCritRate = 5.0;         // 5%基础防爆
        this.activeEffects = new java.util.ArrayList<>();
    }

    // 生命值操作实现
    @Override
    public int getHealth() { return health; }

    @Override
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, constitution * 10));
    }

    @Override
    public void damage(int amount) {
        if (isAlive()) {
            setHealth(health - amount);
            System.out.printf("%s受到%d点伤害，剩余生命值：%d%n", getName(), amount, health);
        }
    }

    @Override
    public void heal(int amount) {
        if (isAlive()) {
            setHealth(health + amount);
            System.out.printf("%s恢复了%d点生命值，当前生命值：%d%n", getName(), amount, health);
        }
    }

    @Override
    public boolean isAlive() {
        return health > 0 && getState() != EntityState.DESTROYED;
    }

    @Override
    public void addEffect(AbstractNonEntities effect) {
        activeEffects.add(effect);
        effect.activate(this);
        System.out.printf("%s获得了效果：%s%n", getName(), effect.getName());
    }

    @Override
    public void removeEffect(AbstractNonEntities effect) {
        if (activeEffects.remove(effect)) {
            effect.deactivate(this);
            System.out.printf("%s失去了效果：%s%n", getName(), effect.getName());
        }
    }

    public void clearEffects() {
        for (AbstractNonEntities effect : new java.util.ArrayList<>(activeEffects)) {
            removeEffect(effect);
        }
    }

    public void activateAllEffects() {
        for (AbstractNonEntities effect : new java.util.ArrayList<>(activeEffects)) {
            if (!effect.tick(this)) {
                removeEffect(effect);
            }
        }
    }

    // 战斗属性Getters
    @Override
    public int getConstitution() { return constitution; }
    @Override
    public int getStrength() { return strength; }
    @Override
    public int getIntelligence() { return intelligence; }
    @Override
    public int getSpeed() { return speed; }
    @Override
    public int getPhysicalDefense() { return physicalDefense; }
    @Override
    public int getMagicDefense() { return magicDefense; }
    @Override
    public double getCritRate() { return critRate; }
    @Override
    public double getAntiCritRate() { return antiCritRate; }
    @Override
    public List<AbstractNonEntities> getActiveEffects() { return activeEffects; }

    // 物品使用实现
    @Override
    public void useItem(ItemTrait item) {
        if (isAlive()) {
            item.use(this);
        } else {
            System.out.println("无法使用物品：实体已死亡");
        }
    }

    @Override
    public void useConsumables(Map<ItemTrait, Integer> consumables) {
        consumables.forEach((item, count) -> {
            for (int i = 0; i < count; i++) {
                useItem(item);
            }
        });
    }

    // 设置属性（提供修改途径）
    public void setStrength(int strength) { this.strength = Math.max(1, strength); }
    public void setIntelligence(int intelligence) { this.intelligence = Math.max(1, intelligence); }
    public void setSpeed(int speed) { this.speed = Math.max(1, speed); }
    public void setPhysicalDefense(int physicalDefense) { this.physicalDefense = Math.max(0, physicalDefense); }
    public void setMagicDefense(int magicDefense) { this.magicDefense = Math.max(0, magicDefense); }
    public void setCritRate(double critRate) { this.critRate = Math.max(0, Math.min(100, critRate)); }
    public void setAntiCritRate(double antiCritRate) { this.antiCritRate = Math.max(0, Math.min(100, antiCritRate)); }
}