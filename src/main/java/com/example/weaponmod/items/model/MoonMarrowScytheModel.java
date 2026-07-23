package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.MoonMarrowScythe;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MoonMarrowScytheModel extends GeoModel<MoonMarrowScythe> {
    @Override
    public ResourceLocation getAnimationResource(MoonMarrowScythe animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/moon_marrow_scythe.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(MoonMarrowScythe animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/moon_marrow_scythe.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MoonMarrowScythe animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/moon_marrow_scythe.png");
    }
}
