package com.example.weaponmod.items;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.*;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WeaponMod.MODID);
    public static final DeferredItem<Item> RUBY = registerItem("ruby", () -> new Item(new Item.Properties().fireResistant()));
    public static final DeferredItem<Item> RAW_RUBY = registerItem("raw_ruby", () -> new Item(new Item.Properties().fireResistant()));
    public static final DeferredItem<Item> NAGINATA_SWORD = registerItem("naginata_sword", NaginataSword::new);
    public static final DeferredItem<Item> HEALING_SCROLL = registerItem("healing_scroll", () -> new HealingScroll(
            new Item.Properties()
                    .durability(10)
                    .setNoRepair()
    ));
    public static final DeferredItem<Item> TEST_SWORD = registerItem("test_sword", TestSword::new);
    public static final DeferredItem<Item> NULL_BLADE = registerItem("null_katana", NullBlade::new);
    public static final DeferredItem<Item> MOON_MARROW_SCYTHE = registerItem("moon_marrow_scythe", MoonMarrowScythe::new);
    public static final DeferredItem<Item> ANTARES_RAPIER = registerItem("antares_rapier", AntaresRapier::new);
    public static final DeferredItem<Item> AZURE_MOUNTAINS_MASHER = registerItem("azure_mountains_masher_sword", AzureMountainsMasher::new);
    public static final DeferredItem<Item> GREAT_APPLE = registerItem("great_apple_heavy_axe", GreatApple::new);


    public static DeferredItem<Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }
}
