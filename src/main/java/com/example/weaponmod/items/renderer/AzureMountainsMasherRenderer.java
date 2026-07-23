package com.example.weaponmod.items.renderer;

import com.example.weaponmod.items.custom.AzureMountainsMasher;
import com.example.weaponmod.items.model.AzureMountainsMasherModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AzureMountainsMasherRenderer extends GeoItemRenderer<AzureMountainsMasher> {
    public AzureMountainsMasherRenderer() {
        super(new AzureMountainsMasherModel());
    }
}
