package com.example.weaponmod.items.renderer;

import com.example.weaponmod.items.custom.NaginataSword;
import com.example.weaponmod.items.model.NaginataModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class NaginataRenderer extends GeoItemRenderer<NaginataSword> {
    public NaginataRenderer() {
        super(new NaginataModel());
    }
}
