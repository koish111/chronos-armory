package com.example.weaponmod.items.renderer;

import com.example.weaponmod.items.custom.AntaresRapier;
import com.example.weaponmod.items.model.AntaresRapierModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AntaresRapierRenderer extends GeoItemRenderer<AntaresRapier> {
    public AntaresRapierRenderer() {
        super(new AntaresRapierModel());
    }
}
