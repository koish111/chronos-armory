package com.example.weaponmod.entities.renderer;

import com.example.weaponmod.entities.custom.BlackFireBallEntity;
import com.example.weaponmod.entities.model.BlackFireBallGeoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackFireBallGeoRenderer extends GeoEntityRenderer<BlackFireBallEntity> {
    public BlackFireBallGeoRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager, new BlackFireBallGeoModel());
    }
}
