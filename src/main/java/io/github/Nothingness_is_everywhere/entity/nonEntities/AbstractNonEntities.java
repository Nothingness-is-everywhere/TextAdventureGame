package io.github.Nothingness_is_everywhere.entity.nonEntities;

import java.util.UUID;

public abstract class AbstractNonEntities {
    private final String id;              // 唯一标识
    private String name;                  // 名称
    private String description;           // 描述

    public AbstractNonEntities(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
    }

    // 通用Getter/Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }

    // 抽象方法：展示效果信息（子类各自实现）
    public abstract String showEffectInfo();
}