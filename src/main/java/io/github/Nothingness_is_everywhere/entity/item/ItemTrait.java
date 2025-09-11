package io.github.Nothingness_is_everywhere.entity.item;

import io.github.Nothingness_is_everywhere.entity.life.AbstractLife;

/**
 * 物品特性接口
 * 定义所有物品的核心行为（使用、堆叠、属性等）
 */
public interface ItemTrait {
    // 物品核心功能：使用效果
    void use(AbstractLife target);

    // 是否可堆叠
    boolean isStackable();

    // 物品重量（基础物理属性）
    int getWeight();

    // 获取物品类型
    ItemType getType();

    // 获取物品名称
    String getName();

    // 获取物品描述
    String getDescription();
}