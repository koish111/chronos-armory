package com.example.weaponmod.effects.cutsom;

import com.example.weaponmod.effects.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 溶解：最大生命值上限降低 (15 + 目标最大生命值 15%)；每次受到伤害时额外受到 3 点伤害
 * （额外伤害见 {@link com.example.weaponmod.events.EffectsEvent} 的 LivingIncomingDamageEvent 钩子）。
 * <p>生命上限削减使用固定 id 的永久属性修饰符实现：效果增删由 EffectsEvent 的 MobEffectEvent
 * 钩子维护，实体进世界时按当前状态同步，服务器崩溃等导致的残留也会被清理。
 */
public class DissolveEffect extends MobEffect {
    public static final ResourceLocation MAX_HEALTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("weaponmod", "dissolve_max_health");
    public static final double FLAT_REDUCTION = 15.0D;
    public static final double PERCENT_REDUCTION = 0.15D;
    public static final float EXTRA_DAMAGE_PER_HIT = 3.0f;

    public DissolveEffect() {
        super(MobEffectCategory.NEUTRAL, 0x6A8A2F);
    }

    /** 削减量 = (15 + 15% × 基础最大生命值) × (等级 + 1)，负值 ADD_VALUE 修饰符。 */
    public static AttributeModifier createMaxHealthModifier(LivingEntity entity, int amplifier) {
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        double base = maxHealth != null ? maxHealth.getBaseValue() : entity.getMaxHealth();
        double amount = -(FLAT_REDUCTION + PERCENT_REDUCTION * base) * (amplifier + 1);
        return new AttributeModifier(MAX_HEALTH_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE);
    }

    /** 按当前效果状态套用或移除生命上限修饰符，可安全重复调用。 */
    public static void applyOrRemoveModifier(LivingEntity entity, boolean active) {
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }
        if (active) {
            MobEffectInstance instance = entity.getEffect(ModEffects.DISSOLVE);
            int amplifier = instance != null ? instance.getAmplifier() : 0;
            maxHealth.addOrReplacePermanentModifier(createMaxHealthModifier(entity, amplifier));
        } else {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER_ID);
        }
    }
}
