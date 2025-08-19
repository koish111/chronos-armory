package com.example.weaponmod.ui;

import com.example.weaponmod.effects.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import com.example.weaponmod.pojo.ShieldData;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.apache.logging.log4j.core.pattern.TextRenderer;

@EventBusSubscriber(modid = "weaponmod")
public class ShieldHudRenderer {
    private static final ResourceLocation SHIELD_ICON =
            ResourceLocation.fromNamespaceAndPath("weaponmod", "textures/ui/bar.png");

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiLayerEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            System.out.println("player is null");
            return;
        }

        if (!player.hasEffect(ModEffects.SHIELD)) return;

        CompoundTag nbt = player.getPersistentData();
        float shield = nbt.getFloat(ShieldData.NBT_KEY);
        if (shield <= 0f) return;

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        // 纹理图真实尺寸
        int textureWidth = 21;
        int textureHeight = 3;

        // 护盾条尺寸直接用纹理图大小
        int barWidth = 21;
        int barHeight = 3;

        // 屏幕中心
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // 护盾条左上角坐标
        int barX = centerX - barWidth / 2;
        int barY = centerY - barHeight / 2;

        // 图标尺寸，这里跟条一样大，不用拉伸
        int iconSize = 3;
        int iconX = barX - iconSize - 4; // 留4像素间距
        int iconY = centerY - iconSize / 2;

        GuiGraphics guiGraphics = event.getGuiGraphics();

        // 绘制图标（用整张图左侧3x3区域，实际上就是整张图）
        guiGraphics.blit(SHIELD_ICON, iconX, iconY, 0, 0, iconSize, iconSize, textureWidth, textureHeight);

        // 绘制护盾条背景（整个21x3）
        guiGraphics.blit(SHIELD_ICON, barX, barY, 0, 0, barWidth, barHeight, textureWidth, textureHeight);

        // 计算当前护盾条宽度（最大21像素）
        int shieldWidth = (int) (barWidth * (shield / 100f));
        if (shieldWidth > 0) {
            guiGraphics.blit(SHIELD_ICON, barX, barY, 0, 0, shieldWidth, barHeight, textureWidth, textureHeight);
        }
    }
}
