package com.example.weaponmod.items.renderer;

import com.example.weaponmod.items.custom.MoonMarrowScythe;
import com.example.weaponmod.items.model.MoonMarrowScytheModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MoonMarrowScytheRenderer extends GeoItemRenderer<MoonMarrowScythe> {
    public MoonMarrowScytheRenderer() {
        super(new MoonMarrowScytheModel());
    }
}
