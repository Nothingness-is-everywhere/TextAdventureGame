package io.github.Nothingness_is_everywhere.entity.nonEntities;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;
import io.github.Nothingness_is_everywhere.entity.base.ElementType;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * 抽象非实体类，表示游戏中非实体对象的基础属性和行为
 * 包含唯一标识、名称、描述等通用字段
 */
public abstract class AbstractNonEntities implements Serializable {
    private final String id;              // 唯一标识
    private String name;                  // 名称
    private final String description;     // 描述
    private ElementType elementType;      // 元素类型
    private int level;                    // 等级
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造器：初始化非实体对象的基础属性(一般应用于构造普通物理技能、天赋和效果)
     * @param name 名称
     * @param description 描述
     */
    public AbstractNonEntities(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.elementType = ElementType.NONE;
        this.level = 1;
    }

    /**
     * 构造器：初始化非实体对象的基础属性和元素类型(一般应用于构造带元素属性的技能、天赋和效果)
     * @param name 名称
     * @param description 描述
     * @param elementType 元素类型
     */
    public AbstractNonEntities(String name, String description, ElementType elementType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.elementType = elementType;
        this.level = 1;
    }

    // 等级相关方法
    /**
     * 获取等级
     * @return 等级
     */
    public int getLevel() {return level;}

    /**
     * 设置等级
     * @param level 等级
     */
    public void setLevel(int level) {
        if (level > 1 && level != this.level) {
            if (level > this.level) {
                for (int i = this.level; i < level; i++) {
                    this.level++;
                    increaseLevel();
                }
            } else {
                for (int i = this.level; i > level; i--) {
                    this.level--;
                    decreaseLevel();
                }
            }
        }
    }

    /**
     * 提升等级
     */
    public abstract void increaseLevel();

    /**
     * 降低等级（最低为1级）
     */
    public abstract void decreaseLevel();

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
     * 获取描述
     * @return description 描述
     */
    public String getDescription() { return description; }

    /**
     * 获取元素类型
     * @return elementType 元素类型
     */
    public ElementType getElementType() { return elementType; }

    /**
     * 设置元素类型
     * @param elementType 元素类型
     */
    public void setElementType(ElementType elementType) { this.elementType = elementType; }

    /**
     * 展示效果信息（抽象方法，需子类实现）
     * @return 效果信息字符串
     */
    public abstract String showEffectInfo();

    /**
     * 触发效果（抽象方法，需子类实现）
     * @param target 目标实体
     * @return 是否成功触发
     */
    public abstract boolean trigger(BaseEntity target);
}