package io.github.Nothingness_is_everywhere.entity.nonEntities.persistent;

import io.github.Nothingness_is_everywhere.entity.base.BaseEntity;

/**
 * 持续性效果接口，用于处理可叠加的游戏状态效果
 */
public interface PersistentEffectTrait {
    /**
     * 检查效果是否仍然有效
     * @param target 效果作用的目标实体
     * @return 如果效果仍然有效则返回true，否则返回false
     */
    boolean isActive(BaseEntity target);

    /**
     * 尝试增加效果的叠加层数
     * @param maxStack 最大可叠加层数
     * @return 如果成功叠加返回true，达到最大层数限制则返回false
     */
    boolean stack(int maxStack);

    /**
     * 尝试减少效果的叠加层数
     * @param minStack 最小叠加层数
     * @return 如果成功减少返回true，达到最小层数限制则返回false
     */
    boolean decreaseStack(int minStack);
}
