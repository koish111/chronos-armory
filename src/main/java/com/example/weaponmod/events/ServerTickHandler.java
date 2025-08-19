package com.example.weaponmod.events;

import com.example.weaponmod.weaponskill.SkillManager;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "weaponmod")
public class ServerTickHandler {
    @SubscribeEvent
    public static void onServerTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            SkillManager.tick(level);
        }
    }
}
