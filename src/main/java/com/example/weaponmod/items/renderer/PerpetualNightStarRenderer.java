package com.example.weaponmod.items.renderer;

import com.example.weaponmod.items.custom.PerpetualNightStar;
import com.example.weaponmod.items.model.PerpetualNightStarModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PerpetualNightStarRenderer extends GeoItemRenderer<PerpetualNightStar> {
    public PerpetualNightStarRenderer() {
        super(new PerpetualNightStarModel());
    }
}
