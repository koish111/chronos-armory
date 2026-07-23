package com.example.weaponmod.events;

import com.example.weaponmod.items.custom.NullBlade;
import com.example.weaponmod.network.NullBladeSkillPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "weaponmod", value = Dist.CLIENT)
public class ClientKeyHandler {
    public static final KeyMapping NULL_BLADE_SKILL_KEY = new KeyMapping(
            "key.weaponmod.null_blade_skill",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.weaponmod"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(NULL_BLADE_SKILL_KEY);
    }

    @EventBusSubscriber(modid = "weaponmod", value = Dist.CLIENT)
    public static class KeyInputHandler {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (event.getAction() == GLFW.GLFW_PRESS && NULL_BLADE_SKILL_KEY.matches(event.getKey(), event.getScanCode())) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    var mainHand = mc.player.getMainHandItem();
                    var offHand = mc.player.getOffhandItem();
                    if (mainHand.getItem() instanceof NullBlade || offHand.getItem() instanceof NullBlade) {
                        PacketDistributor.sendToServer(new NullBladeSkillPacket());
                    }
                }
            }
        }
    }
}
