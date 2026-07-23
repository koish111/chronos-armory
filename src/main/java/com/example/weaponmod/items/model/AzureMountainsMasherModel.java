package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.AzureMountainsMasher;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AzureMountainsMasherModel extends GeoModel<AzureMountainsMasher> {
    @Override
    public ResourceLocation getAnimationResource(AzureMountainsMasher animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/azure_mountains_masher_sword.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(AzureMountainsMasher animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/azure_mountains_masher_sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AzureMountainsMasher animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/azure_mountains_masher_sword.png");
    }
}
