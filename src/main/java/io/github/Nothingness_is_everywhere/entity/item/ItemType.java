package io.github.Nothingness_is_everywhere.entity.item;

// 物品类型：用于分类物品功能
public enum ItemType {
    WEAPON("武器"),
    KEY("钥匙"),
    CONSUMABLE("消耗品");

    // 中文描述
    private final String chineseDesc;

    // 构造方法，初始化中文描述
    ItemType(String chineseDesc) {
        this.chineseDesc = chineseDesc;
    }

    // 获取中文描述
    public String getChineseDesc() {
        return chineseDesc;
    }
}