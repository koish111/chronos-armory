package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.NaginataSword;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NaginataModel extends GeoModel<NaginataSword> {
    @Override
    public ResourceLocation getModelResource(NaginataSword object) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/naginata_sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NaginataSword object) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/naginata_sword.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NaginataSword object) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/naginata_sword.animation.json");
    }
}
