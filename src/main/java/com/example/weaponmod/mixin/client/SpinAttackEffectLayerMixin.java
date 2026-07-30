package com.example.weaponmod.mixin.client;

import com.example.weaponmod.items.custom.NullBlade;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpinAttackEffectLayer.class)
public abstract class SpinAttackEffectLayerMixin {
    @Redirect(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isAutoSpinAttack()Z"
            )
    )
    private boolean weaponmod$hideNullBladeSpinEffect(LivingEntity entity) {
        if (!entity.isAutoSpinAttack()) {
            return false;
        }

        boolean holdingNullBlade = entity.getMainHandItem().getItem() instanceof NullBlade
                || entity.getOffhandItem().getItem() instanceof NullBlade;
        return !holdingNullBlade;
    }
}
