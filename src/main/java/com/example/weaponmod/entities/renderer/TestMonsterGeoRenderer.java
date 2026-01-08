package com.example.weaponmod.entities.renderer;

import com.example.weaponmod.entities.custom.TestMonster;
import com.example.weaponmod.entities.model.TestMonsterGeoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TestMonsterGeoRenderer extends GeoEntityRenderer<TestMonster> {
    public TestMonsterGeoRenderer(EntityRendererProvider.Context rendererManger) {
        super(rendererManger, new TestMonsterGeoModel());
    }
}
