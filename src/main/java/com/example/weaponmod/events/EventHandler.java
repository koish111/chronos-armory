package com.example.weaponmod.events;

import com.example.weaponmod.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "weaponmod")
public class EventHandler {
    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        if(!(event.getSource().getEntity() instanceof LivingEntity)) return;
        if(isHoldingCurseBlade(player)) {
            player.setHealth(0.0F);
            player.sendSystemMessage(Component.literal("你被诅咒的力量吞噬了"));
        }
    }

    private static boolean isHoldingCurseBlade(Player player) {
        return player.getMainHandItem().getItem() == ModItems.NAGINATA_SWORD.get() ||
                player.getOffhandItem().getItem() == ModItems.NAGINATA_SWORD.get();
    }
}
