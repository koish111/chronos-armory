package com.example.weaponmod.entities.model;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.entities.custom.TestMonster;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TestMonsterGeoModel extends GeoModel<TestMonster> {
    @Override
    public ResourceLocation getModelResource(TestMonster testMonster) {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "geo/test_monster.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TestMonster testMonster) throws NullPointerException {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "textures/entity/test_monster.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TestMonster testMonster) throws NullPointerException {
        return ResourceLocation.fromNamespaceAndPath(WeaponMod.MODID, "animations/test_monster.animation.json");
    }
}
