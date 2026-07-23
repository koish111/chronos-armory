package com.example.weaponmod.items.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.AntaresRapier;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AntaresRapierModel extends GeoModel<AntaresRapier> {
    @Override
    public ResourceLocation getAnimationResource(AntaresRapier animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/antares_rapier.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(AntaresRapier animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/antares_rapier.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AntaresRapier animatable) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/item/antares_rapier.png");
    }
}
