package com.example.weaponmod.effects.cutsom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BlackfireEffect extends MobEffect {
    public BlackfireEffect() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            float maxHealth = livingEntity.getMaxHealth();
            // 基础伤害 每隔0.25秒造成 (2 + 0.5 * 等级) + 生命值上限 * (0.1 + 0.005 * 等级) 的伤害
            float damage = (2.0f + 0.5f * amplifier) + maxHealth * (0.01f + 0.005f * amplifier);
            livingEntity.hurt(livingEntity.damageSources().magic(), damage);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 5 == 0;
    }

}
