package com.example.weaponmod.events;

import com.example.weaponmod.attachments.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

/**
 * 无形效果的客户端隐形渲染：PvP 开启时（服务端同步的 UNSEEN_PVP_INVISIBLE 标记）
 * 取消玩家整体渲染（含护甲、手持物品与名牌）与第一人称手臂渲染。
 */
@EventBusSubscriber(modid = "weaponmod", value = Dist.CLIENT)
public class UnseenClientHandler {

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (event.getEntity() instanceof Player player
                && player.getData(ModAttachments.UNSEEN_PVP_INVISIBLE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getData(ModAttachments.UNSEEN_PVP_INVISIBLE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderArm(RenderArmEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        if (player.getData(ModAttachments.UNSEEN_PVP_INVISIBLE)) {
            event.setCanceled(true);
        }
    }
}
