package com.example.weaponmod.effects.cutsom;

import com.example.weaponmod.effects.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 黑焰：每 0.25 秒造成 (1.5 + 目标最大生命值 0.5%) 的魔法伤害（× 等级系数）。
 * 每次结算前计算剩余时长内的预期持续总伤害，若高于敌人剩余生命值则直接斩杀。
 * <p>NEUTRAL 分类 + 自定义效果 id：原版 {@code LivingEntity#canBeAffected} 只针对
 * INFESTED/OOZING/REGENERATION/POISON 等原版效果做免疫，本效果对所有实体（含凋灵、不死生物）都不会被免疫。
 */
public class BlackfireEffect extends MobEffect {
    public static final int TICK_INTERVAL = 5;

    public BlackfireEffect() {
        super(MobEffectCategory.NEUTRAL, 0x4B0082);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return true;
        }
        // 对创造模式玩家无效（覆盖生存期间被施加、之后切成创造的情况）
        if (entity instanceof Player player && player.isCreative()) {
            return true;
        }

        float maxHealth = entity.getMaxHealth();
        float damagePerTick = (1.5f + maxHealth * 0.005f) * (amplifier + 1);

        // 预期持续伤害 = 剩余时长内剩余触发次数 × 单次伤害；高于剩余生命值则直接斩杀
        MobEffectInstance instance = entity.getEffect(ModEffects.BLACKFIRE);
        if (instance != null) {
            int remainingHits = Math.max(0, instance.getDuration() / TICK_INTERVAL);
            if (remainingHits * damagePerTick > entity.getHealth()) {
                entity.kill();
                return true;
            }
        }

        // 遵循仓库惯例：清无敌帧结算后还原，保证 0.25 秒节奏不被无敌帧吞掉
        int originalInvulnerableTime = entity.invulnerableTime;
        entity.invulnerableTime = 0;
        entity.hurt(entity.damageSources().magic(), damagePerTick);
        entity.invulnerableTime = originalInvulnerableTime;
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % TICK_INTERVAL == 0;
    }
}
