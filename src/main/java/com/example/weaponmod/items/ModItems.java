package com.example.weaponmod.items;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.items.custom.HealingScroll;
import com.example.weaponmod.items.custom.NaginataSword;
import com.example.weaponmod.items.custom.NullBlade;
import com.example.weaponmod.items.custom.TestSword;
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


    public static DeferredItem<Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }
}
