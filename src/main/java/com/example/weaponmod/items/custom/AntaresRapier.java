package com.example.weaponmod.items.custom;

import com.example.weaponmod.items.renderer.AntaresRapierRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Unbreakable;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class AntaresRapier extends SwordItem implements GeoItem {
    public static final ResourceLocation WEAPON_ID = ResourceLocation.fromNamespaceAndPath("weaponmod", "antares_rapier");
    public static final int MAX_CHARGE = 400;
    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AntaresRapier() {
        super(Tiers.NETHERITE, new Item.Properties().
                attributes(SwordItem.createAttributes(Tiers.NETHERITE, 10, -2.2f))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(false)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private AntaresRapierRenderer renderer;
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new AntaresRapierRenderer();
                return this.renderer;
            }
        });
    }
}
