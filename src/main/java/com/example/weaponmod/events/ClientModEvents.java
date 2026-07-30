package com.example.weaponmod.events;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.entities.ModEntities;
import com.example.weaponmod.entities.renderer.BlackFireBallGeoRenderer;
import com.example.weaponmod.particles.ModParticles;
import com.example.weaponmod.particles.custom.BrimstoneParticle;
import com.example.weaponmod.particles.custom.NullBladeParticle;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = "weaponmod")
public class ClientModEvents {
    public static final ModelLayerLocation BLACK_FIRE_BALL = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "black_fire_ball"), "main");

//    @SubscribeEvent
//    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        event.registerLayerDefinition(BLACK_FIRE_BALL, BlackFireBallGeoModel::createBodyLayer);
//    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BLACK_FIRE_BALL.get(), BlackFireBallGeoRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.BRIMSTONE.get(), BrimstoneParticle.Provider::new);
        event.registerSpriteSet(ModParticles.NULL_BLADE.get(), NullBladeParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "item/null_katana_gui")
        ));
    }
}
