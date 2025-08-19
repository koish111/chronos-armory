package com.example.weaponmod.items.custom;

import com.example.weaponmod.effects.ModEffects;
import com.example.weaponmod.items.renderer.NaginataRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class NaginataSword extends SwordItem implements GeoItem {
    public NaginataSword() {
        super(Tiers.DIAMOND, new SwordItem.Properties().
                attributes(SwordItem.createAttributes(Tiers.DIAMOND, 16, -2.3f)));
    }

    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration % 20 == 0){
            livingEntity.addEffect(new MobEffectInstance(MobEffects.HARM,10,0));
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float damage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        target.addEffect(new MobEffectInstance(ModEffects.BLACKFIRE, 200, 1));

        if (isBacksTab(target, attacker)) {
            damage *= 1.5f;
            if (attacker instanceof Player player) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
        target.hurt(target.damageSources().mobAttack(attacker), damage);
        return true;
    }

    private boolean isBacksTab(LivingEntity target, LivingEntity attacker) {
        Vec3 attackerPos = attacker.getEyePosition(1.0f);
        Vec3 targetPos = target.getEyePosition(1.0f);
        Vec3 attackerToTarget = targetPos.subtract(attackerPos).normalize();
        Vec3 targetForward = target.getViewVector(1.0f);
        double dotProduct = attackerToTarget.dot(targetForward);
        return dotProduct > 0.3;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            long id = GeoItem.getId(stack);
            if (id != -1) {
                triggerAnim(player, id, "Activation", "idle");
            }
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activation",
                0, state -> PlayState.CONTINUE)
                .triggerableAnim("idle", ACTIVATE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private NaginataRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new NaginataRenderer();

                return this.renderer;
            }
        });
    }


}
