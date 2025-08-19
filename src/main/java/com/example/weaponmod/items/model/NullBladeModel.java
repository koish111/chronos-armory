package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.NaginataSword;
import com.example.weaponmod.items.custom.NullBlade;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NullBladeModel extends GeoModel<NullBlade> {
    @Override
    public ResourceLocation getModelResource(NullBlade object) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/null_katana.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NullBlade object) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/null_katana.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NullBlade object) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/null_katana.animation.json");
    }
}
