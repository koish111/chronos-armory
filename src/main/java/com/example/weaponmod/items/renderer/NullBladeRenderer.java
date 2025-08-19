package com.example.weaponmod.items.renderer;

import com.example.weaponmod.items.custom.NullBlade;
import com.example.weaponmod.items.model.NullBladeModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class NullBladeRenderer extends GeoItemRenderer<NullBlade> {
    public NullBladeRenderer() {
        super(new NullBladeModel());
    }
}
