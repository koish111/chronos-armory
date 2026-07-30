package com.example.weaponmod.attachments;

import com.example.weaponmod.WeaponMod;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, WeaponMod.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> NULL_BLADE_CHARGE =
            registerCharge("null_blade_charge");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> MOON_MARROW_SCYTHE_CHARGE =
            registerCharge("moon_marrow_scythe_charge");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ANTARES_RAPIER_CHARGE =
            registerCharge("antares_rapier_charge");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> AZURE_MOUNTAINS_MASHER_CHARGE =
            registerCharge("azure_mountains_masher_charge");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> GREAT_APPLE_CHARGE =
            registerCharge("great_apple_charge");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> PERPETUAL_NIGHT_STAR_CHARGE =
            registerCharge("perpetual_night_star_charge");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> CYAN_FROST_VIOLET_VOLT_CHARGE =
            registerCharge("cyan_frost_violet_volt_charge");

    private static DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> registerCharge(String name) {
        return ATTACHMENT_TYPES.register(
                name,
                () -> AttachmentType.builder(() -> 0)
                        .serialize(Codec.INT)
                        .sync(ByteBufCodecs.VAR_INT)
                        .copyOnDeath()
                        .build());
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
