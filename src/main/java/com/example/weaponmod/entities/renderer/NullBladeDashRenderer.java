package com.example.weaponmod.entities.renderer;

import com.example.weaponmod.entities.custom.NullBladeDash;
import com.example.weaponmod.entities.model.NullBladeDashModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NullBladeDashRenderer extends GeoEntityRenderer<NullBladeDash> {
    public NullBladeDashRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NullBladeDashModel()); this.shadowRadius = 0f; // 无阴影
    }

    @Override
    public RenderType getRenderType(NullBladeDash animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture); // 常用于特效
    }

    @Override
    public void render(
            NullBladeDash entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();

        // ⭐ 平滑插值（防抖）
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

        // ⭐ 关键：让模型朝向玩家方向
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));

        // （可选）如果需要俯仰，比如向上/向下冲刺
        // float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        // poseStack.mulPose(Axis.XP.rotationDegrees(pitch));

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
