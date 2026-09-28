package com.example.weaponmod.potions;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.effects.ModEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** AntaresRapier 衍生效果的测试药水（黑焰基础 6 秒 / 溶解、无形基础 10 秒），供游戏内验证用。 */
public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, WeaponMod.MODID);

    public static final DeferredHolder<Potion, Potion> BLACKFIRE =
            POTIONS.register("blackfire", () -> new Potion(new MobEffectInstance(ModEffects.BLACKFIRE, 120)));

    public static final DeferredHolder<Potion, Potion> DISSOLVE =
            POTIONS.register("dissolve", () -> new Potion(new MobEffectInstance(ModEffects.DISSOLVE, 200)));

    public static final DeferredHolder<Potion, Potion> UNSEEN =
            POTIONS.register("unseen", () -> new Potion(new MobEffectInstance(ModEffects.UNSEEN, 200)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
