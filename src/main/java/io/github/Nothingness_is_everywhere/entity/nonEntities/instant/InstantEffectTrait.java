package io.github.Nothingness_is_everywhere.entity.nonEntities.instant;

import io.github.Nothingness_is_everywhere.entity.life.LifeTrait;

public interface InstantEffectTrait {
    void updateCooldown();               // 更新冷却
    boolean canTrigger(LifeTrait caster); // 检查是否可触发
    boolean hasEnoughResource(LifeTrait caster); // 检查资源
}
