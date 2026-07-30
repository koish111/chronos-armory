package com.example.weaponmod.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("autoSpinAttackTicks")
    void weaponmod$setAutoSpinAttackTicks(int ticks);

    @Accessor("autoSpinAttackDmg")
    void weaponmod$setAutoSpinAttackDamage(float damage);

    @Accessor("autoSpinAttackItemStack")
    void weaponmod$setAutoSpinAttackItemStack(ItemStack itemStack);

    @Invoker("setLivingEntityFlag")
    void weaponmod$setLivingEntityFlag(int key, boolean value);
}
