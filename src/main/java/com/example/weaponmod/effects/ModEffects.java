package com.example.weaponmod.effects;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.effects.cutsom.BlackfireEffect;
import com.example.weaponmod.effects.cutsom.DissolveEffect;
import com.example.weaponmod.effects.cutsom.ShieldEffect;
import com.example.weaponmod.effects.cutsom.UnseenEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, WeaponMod.MODID);

    public static final DeferredHolder<MobEffect, BlackfireEffect> BLACKFIRE =
            EFFECTS.register("blackfire", BlackfireEffect::new);

    public static final DeferredHolder<MobEffect, DissolveEffect> DISSOLVE =
            EFFECTS.register("dissolve", DissolveEffect::new);

    public static final DeferredHolder<MobEffect, UnseenEffect> UNSEEN =
            EFFECTS.register("unseen", UnseenEffect::new);

    public static final DeferredHolder<MobEffect, ShieldEffect> SHIELD =
            EFFECTS.register("shield", registryName -> new ShieldEffect(MobEffectCategory.BENEFICIAL, 0x00BFFF));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
