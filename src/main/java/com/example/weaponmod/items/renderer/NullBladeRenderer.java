package com.example.weaponmod.items.renderer;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.NullBlade;
import com.example.weaponmod.items.model.NullBladeModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class NullBladeRenderer extends GeoItemRenderer<NullBlade> {
    private static final ModelResourceLocation GUI_MODEL = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "item/null_katana_gui")
    );

    public NullBladeRenderer() {
        super(new NullBladeModel());
    }

    @Override
    protected void renderInGui(
            ItemDisplayContext transformType,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            float partialTick
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel guiModel = minecraft.getModelManager().getModel(GUI_MODEL);

        // 抵消外层 builtin/entity 渲染在调用 BEWLR 前施加的 -0.5 平移，
        // 再让原版 ItemRenderer 正常应用 item/generated 的 GUI 变换。
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        minecraft.getItemRenderer().render(
                this.currentItemStack,
                ItemDisplayContext.GUI,
                false,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                guiModel
        );
        poseStack.popPose();
    }
}
