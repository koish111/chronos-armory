package com.example.weaponmod.events;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.entities.ModEntities;
import com.example.weaponmod.entities.model.BlackFireBallGeoModel;
import com.example.weaponmod.entities.renderer.BlackFireBallGeoRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

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
}
