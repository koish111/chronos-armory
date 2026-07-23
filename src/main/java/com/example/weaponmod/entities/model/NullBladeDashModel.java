package com.example.weaponmod.entities.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.entities.custom.BlackFireBallEntity;
import com.example.weaponmod.entities.custom.NullBladeDash;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NullBladeDashModel extends GeoModel<NullBladeDash> {
    @Override
    public ResourceLocation getModelResource(NullBladeDash animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/dash.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NullBladeDash animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/entity/dash.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NullBladeDash animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/dash.animation.json");
    }
}
