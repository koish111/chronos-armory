package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.GreatApple;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GreatAppleModel extends GeoModel<GreatApple> {
    @Override
    public ResourceLocation getAnimationResource(GreatApple animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/great_apple_heavy_axe.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(GreatApple animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/great_apple_heavy_axe.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GreatApple animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/great_apple_heavy_axe.png");
    }
}
