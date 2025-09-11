package io.github.Nothingness_is_everywhere.entity.scene;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 地图场景接口，定义所有场景的核心交互行为
 * 所有具体场景（如森林、城市、洞穴）都需实现此接口
 */
public interface Scene extends Serializable {
    // 获取场景唯一标识
    String getId();

    // 获取场景名称
    String getName();

    // 获取场景描述（用于展示给玩家）
    String getDescription();

    // 检查实体是否在当前场景内（基于坐标范围）
    boolean isEntityInScene(BaseEntity entity);

    // 向场景添加实体（如玩家进入、物品掉落）
    void addEntity(BaseEntity entity);

    // 从场景移除实体（如玩家离开、物品被拾取）
    boolean removeEntity(BaseEntity entity);

    // 获取场景内的所有实体
    List<BaseEntity> getEntities();

    // 获取场景内指定类型的实体（如所有生命实体、所有物品）
    <T extends BaseEntity> List<T> getEntitiesByType(Class<T> type);

    // 获取相邻场景（用于场景切换，如从森林进入洞穴）
    List<String> getAdjacentScenes();

    // 添加相邻场景（建立场景连接）
    void addAdjacentScene(String sceneId);
}
