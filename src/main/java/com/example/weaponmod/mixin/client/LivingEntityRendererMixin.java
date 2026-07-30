package com.example.weaponmod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.example.weaponmod.items.custom.NullBlade;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Redirect(
            method = "setupRotations",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    ordinal = 3
            )
    )
    private void weaponmod$keepNullBladeDashDirection(
            PoseStack poseStack,
            Quaternionf rotation,
            LivingEntity entity,
            PoseStack methodPoseStack,
            float bob,
            float bodyRotation,
            float partialTick,
            float scale
    ) {
        boolean holdingNullBlade = entity.getMainHandItem().getItem() instanceof NullBlade
                || entity.getOffhandItem().getItem() instanceof NullBlade;
        if (!holdingNullBlade) {
            poseStack.mulPose(rotation);
        }
    }
}
