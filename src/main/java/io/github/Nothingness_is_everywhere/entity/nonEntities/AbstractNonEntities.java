package io.github.Nothingness_is_everywhere.entity.nonEntities;

import java.util.UUID;

/**
 * 抽象非实体类，表示游戏中非实体对象的基础属性和行为
 * 包含唯一标识、名称、描述等通用字段
 */
public abstract class AbstractNonEntities {
    private final String id;              // 唯一标识
    private String name;                  // 名称
    private String description;           // 描述

    /**
     * 构造器：初始化非实体对象的基础属性
     * @param name 名称
     * @param description 描述
     */
    public AbstractNonEntities(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
    }

    /**
     * 获取唯一标识
     * @return id 唯一标识
     */
    public String getId() { return id; }

    /**
     * 获取名称
     * @return name 名称
     */
    public String getName() { return name; }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) { this.name = name; }

    /**
     * 获取描述
     * @return description 描述
     */
    public String getDescription() { return description; }

    /**
     * 展示效果信息（抽象方法，需子类实现）
     * @return 效果信息字符串
     */
    public abstract String showEffectInfo();
}