package io.github.Nothingness_is_everywhere.entity.base;

/**
 * 元素属性枚举类
 * <p>
 * 用于表示游戏中各种元素属性类型（如火、水、风、土等），
 * 每个枚举值包含对应的中文描述，便于界面显示和逻辑判定。
 * NONE 表示无属性（防止空指针），CHAOS 表示混沌属性（包含多种元素但无特殊效果）。
 */
public enum ElementType {
    NONE("无属性"),
    FIRE("火元素"),
    WATER("水元素"),
    WIND("风元素"),
    EARTH("土元素"),
    LIGHTNING("雷元素"),
    LIGHT("光元素"),
    DARK("暗元素"),
    CHAOS("混沌属性");

    // 中文描述
    private final String chineseDesc;

    /**
     * 构造方法，初始化中文描述
     * @param chineseDesc 中文描述
     */
    ElementType(String chineseDesc) {
        this.chineseDesc = chineseDesc;
    }

    /**
     * 获取中文描述
     * @return 中文描述
     */
    public String getChineseDesc() {
        return chineseDesc;
    }
}