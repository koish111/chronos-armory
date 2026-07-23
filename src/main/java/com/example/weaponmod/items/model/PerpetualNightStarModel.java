package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.PerpetualNightStar;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PerpetualNightStarModel extends GeoModel<PerpetualNightStar> {
    @Override
    public ResourceLocation getAnimationResource(PerpetualNightStar animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/perpetual_nightstar_trident.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(PerpetualNightStar animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/perpetual_nightstar_trident.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PerpetualNightStar animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/perpetual_nightstar_trident.png");
    }
}
