package io.github.Nothingness_is_everywhere.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 场景抽象类，封装所有场景的通用属性和基础逻辑
 * 具体场景（如森林、城市）可继承此类并扩展个性化功能
 */
public abstract class AbstractScene implements Scene {
    private static final long serialVersionUID = 1L; // 支持序列化

    protected final String id;               // 场景唯一标识（类似地图ID）
    protected String name;                   // 场景名称（如"迷雾森林"）
    protected String description;            // 场景描述（如"布满荆棘的森林，能见度低"）
    protected int minX, minY;                // 场景范围：左上角X/Y坐标
    protected int maxX, maxY;                // 场景范围：右下角X/Y坐标
    protected int Z;                     // 场景高度（Z坐标，默认0表示地面）
    protected List<BaseEntity> entities;     // 场景内的实体列表（玩家、NPC、物品等）
    protected List<String> adjacentScenes;   // 相邻场景的ID（用于场景切换）

    /**
     * 构造器：初始化场景的基础属性和空间范围
     * @param name 场景名称
     * @param description 场景描述
     * @param minX 左上角X坐标
     * @param minY 左上角Y坐标
     * @param maxX 右下角X坐标
     * @param maxY 右下角Y坐标
     * @param Z  场景高度（默认0表示地面）
     */
    public AbstractScene(String name, String description,
                         int minX, int minY, int maxX, int maxY, int Z) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.Z = Z;
        this.entities = new ArrayList<>();
        this.adjacentScenes = new ArrayList<>();
    }

    // 检查实体是否在场景范围内（核心空间逻辑）
    @Override
    public boolean isEntityInScene(BaseEntity entity) {
        int x = entity.getX();
        int y = entity.getY();
        int z = entity.getZ();
        // 实体坐标需在场景的min和max之间（包含边界）
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z == Z;
    }

    // 添加实体（自动校验是否在场景范围内）
    @Override
    public void addEntity(BaseEntity entity) {
        if (isEntityInScene(entity)) {
            entities.add(entity);
            System.out.printf("实体【%s】已进入场景【%s】%n", entity.getName(), name);
        } else {
            System.err.printf("实体【%s】不在场景【%s】范围内，无法添加%n", entity.getName(), name);
        }
    }

    // 移除实体（返回是否移除成功）
    @Override
    public boolean removeEntity(BaseEntity entity) {
        boolean removed = entities.remove(entity);
        if (removed) {
            System.out.printf("实体【%s】已离开场景【%s】%n", entity.getName(), name);
        }
        return removed;
    }

    // 获取场景内所有实体
    @Override
    public List<BaseEntity> getEntities() {
        return new ArrayList<>(entities); // 返回副本，防止外部直接修改
    }

    // 获取指定类型的实体（如筛选出所有生命实体）
    @Override
    public <T extends BaseEntity> List<T> getEntitiesByType(Class<T> type) {
        return entities.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    // 获取相邻场景
    @Override
    public List<String> getAdjacentScenes() {
        return new ArrayList<>(adjacentScenes); // 返回副本，防止外部直接修改
    }

    // 添加相邻场景（建立连接）
    @Override
    public void addAdjacentScene(String sceneId) {
        if (!adjacentScenes.contains(sceneId)) {
            adjacentScenes.add(sceneId);
            System.out.printf("场景【%s】与【%s】建立了连接%n", name, sceneId);
        }
    }

    // Getter
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    // 允许子类重写场景信息展示
    public abstract String showSceneInfo();
}
