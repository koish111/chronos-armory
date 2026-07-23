package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.CyanFrostVioletVolt;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class CyanFrostVioletVoltModel extends GeoModel<CyanFrostVioletVolt> {
    @Override
    public ResourceLocation getAnimationResource(CyanFrostVioletVolt animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/cyanfrost_violetvolt_katana.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(CyanFrostVioletVolt animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/cyanfrost_violetvolt_katana.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CyanFrostVioletVolt animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/cyanfrost_violetvolt_katana.png");
    }
}
