package com.example.weaponmod;

import com.example.weaponmod.effects.ModEffects;
import com.example.weaponmod.entities.ModEntities;
import com.example.weaponmod.items.ModItems;
import com.example.weaponmod.items.custom.NaginataSword;
import com.example.weaponmod.network.ChargeSyncPacket;
import com.example.weaponmod.network.NullBladeSkillPacket;
import com.example.weaponmod.particles.ModParticles;
import com.example.weaponmod.sounds.ModSounds;
import com.example.weaponmod.ui.ModTabs;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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
        ModEntities.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModSounds.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onRegisterPayloads);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GeoItem.registerSyncedAnimatable((GeoItem) ModItems.NAGINATA_SWORD.get());
        });
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToServer(NullBladeSkillPacket.TYPE, NullBladeSkillPacket.CODEC, NullBladeSkillPacket::handle);
        registrar.playToClient(ChargeSyncPacket.TYPE, ChargeSyncPacket.CODEC, ChargeSyncPacket::handle);
    }
}
