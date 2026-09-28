package com.example.weaponmod.effects.cutsom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 无形：脱离所有敌人的仇恨，且持续期间不会被敌人选定为目标（目标切换由 EffectsEvent 的
 * LivingChangeTargetEvent 钩子拦截）。PvP 开启时的完全隐形由服务端通过
 * {@link com.example.weaponmod.attachments.ModAttachments#UNSEEN_PVP_INVISIBLE} 同步标记，
 * 客户端在 UnseenClientHandler 中取消渲染（含护甲与手持物品）。
 */
public class UnseenEffect extends MobEffect {
    public static final double AGGRO_CLEAR_RADIUS = 64.0D;

    public UnseenEffect() {
        super(MobEffectCategory.NEUTRAL, 0x9A9AA8);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return;
        }
        clearAggro(entity);
    }

    /** 让周围所有以该实体为目标的生物放弃仇恨（后续再锁定由 LivingChangeTargetEvent 拦截）。 */
    public static void clearAggro(LivingEntity entity) {
        AABB area = entity.getBoundingBox().inflate(AGGRO_CLEAR_RADIUS);
        List<Mob> mobs = entity.level().getEntitiesOfClass(Mob.class, area);
        for (Mob mob : mobs) {
            if (mob.getTarget() == entity) {
                mob.setTarget(null);
            }
            if (mob.getLastHurtByMob() == entity) {
                mob.setLastHurtByMob(null);
            }
        }
    }
}
