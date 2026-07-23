package com.example.weaponmod.items.renderer;

import com.example.weaponmod.items.custom.GreatApple;
import com.example.weaponmod.items.model.GreatAppleModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GreatAppleRenderer extends GeoItemRenderer<GreatApple> {
    public GreatAppleRenderer() {
        super(new GreatAppleModel());
    }
}
