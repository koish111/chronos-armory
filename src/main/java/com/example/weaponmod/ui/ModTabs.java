package com.example.weaponmod.ui;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WeaponMod.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NEO_EXAMPLEMOD =
            CREATIVE_TABS.register("neo_weaponmod", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.neo_weaponmod"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.RUBY.get().getDefaultInstance())
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.RUBY.get());
                        output.accept(ModItems.RAW_RUBY.get());
                        output.accept(ModItems.NAGINATA_SWORD.get());
                        output.accept(ModItems.HEALING_SCROLL.get());
                        output.accept(ModItems.TEST_SWORD.get());
                        output.accept(ModItems.NULL_BLADE.get());
                        output.accept(ModItems.MOON_MARROW_SCYTHE.get());
                    }).build());
}
