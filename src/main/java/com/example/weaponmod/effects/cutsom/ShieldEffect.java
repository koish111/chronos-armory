package com.example.weaponmod.effects.cutsom;

import com.example.weaponmod.pojo.ShieldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ShieldEffect extends MobEffect {
    public ShieldEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // 当效果被添加（或重加）时调用 —— 用 amplifier 决定给多少护盾（示例：每级 20 点）
    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);

        CompoundTag nbt = entity.getPersistentData();
        float current = nbt.getFloat(ShieldData.NBT_KEY);
        float added = (amplifier + 1) * 20f; // 示例：Amplifier 0 => +20 护盾
        nbt.putFloat(ShieldData.NBT_KEY, current + added);

        // 仅在服务器端发消息
        if (!entity.level().isClientSide()) {
            entity.sendSystemMessage(
                    Component.literal("[护盾测试] 获得护盾: " + (current + added))
            );
        }
    }

    // 可选：当效果被移除/结束时可以清理（视需求）
    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, net.minecraft.world.entity.Entity.RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        // 例如：不自动清除 NBT；或者按需要清掉
    }
}
