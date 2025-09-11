package io.github.Nothingness_is_everywhere.entity;

/**
 * 物品特性接口（类似“物品分子结构”）
 * 所有物品必须实现此接口，表达“物品”相关的核心行为
 */
public interface ItemTrait {
    // 物品使用效果（物品的核心功能）
    void use(AbstractLife target);

    // 物品是否可堆叠
    boolean isStackable();

    // 物品重量（基础物理属性）
    int getWeight();

    // 获取物品类型
    ItemType getType();
}
