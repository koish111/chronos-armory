package com.example.weaponmod;

import com.example.weaponmod.effects.ModEffects;
import com.example.weaponmod.entities.ModEntities;
import com.example.weaponmod.items.ModItems;
import com.example.weaponmod.items.custom.NaginataSword;
import com.example.weaponmod.ui.ModTabs;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoItem;


@Mod(WeaponMod.MODID)
public class WeaponMod {
    public static final String MODID = "weaponmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WeaponMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_TABS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GeoItem.registerSyncedAnimatable((GeoItem) ModItems.NAGINATA_SWORD.get());
        });
    }
}
