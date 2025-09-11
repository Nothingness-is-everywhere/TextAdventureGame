package io.github.Nothingness_is_everywhere.entity;

/**
 * 森林场景（具体场景示例）
 * 继承抽象场景类，添加森林特有的环境效果和事件
 */
public class ForestScene extends AbstractScene {
    private boolean isFoggy; // 森林特有属性：是否起雾（影响能见度）
    private int monsterDensity; // 森林特有属性：怪物密度（影响遇敌概率）

    public ForestScene(String name, String description,
                       int minX, int minY, int maxX, int maxY, int Z) {
        super(name, description, minX, minY, maxX, maxY, Z);
        this.isFoggy = false; // 初始无雾
        this.monsterDensity = 3; // 中等怪物密度
    }

    // 森林特有方法：触发雾天效果（降低能见度，影响实体感知）
    public void triggerFog() {
        this.isFoggy = true;
        System.out.printf("场景【%s】起雾了，能见度降低！%n", getName());
        // 雾天对场景内生命实体的影响（如感知范围缩小）
//        getEntitiesByType(LifeTrait.class).forEach(life -> {
//            System.out.printf("【%s】在雾中看不清远处...%n", ((BaseEntity) life).getName());
//        });
    }

    // 森林特有方法：计算遇敌概率（基于怪物密度）
    public double calculateEncounterRate() {
        // 雾天会增加遇敌概率
        return isFoggy ? (monsterDensity * 0.2) : (monsterDensity * 0.1);
    }

    // 重写场景信息展示，包含森林特有属性
    @Override
    public String showSceneInfo() {
        return String.format(
                "【场景：%s】%n描述：%s%n范围：(%d,%d)-(%d,%d)%n状态：%s%n怪物密度：%d%n相邻场景：%s",
                getName(),
                getDescription(),
                minX, minY, maxX, maxY,
                isFoggy ? "大雾弥漫" : "天气晴朗",
                monsterDensity,
                getAdjacentScenes()
        );
    }
}
