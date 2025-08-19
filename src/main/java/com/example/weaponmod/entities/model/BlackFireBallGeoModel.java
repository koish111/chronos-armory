package com.example.weaponmod.entities.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.entities.custom.BlackFireBallEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlackFireBallGeoModel extends GeoModel<BlackFireBallEntity> {
    @Override
    public ResourceLocation getModelResource(BlackFireBallEntity blackFireBallEntity) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/black_fire_ball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlackFireBallEntity blackFireBallEntity) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/entity/black_fire_ball.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlackFireBallEntity blackFireBallEntity) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/black_fire_ball.animation.json");
    }
}
