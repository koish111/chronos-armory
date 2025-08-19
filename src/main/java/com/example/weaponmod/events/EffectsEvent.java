package com.example.weaponmod.events;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.effects.ModEffects;
import com.example.weaponmod.effects.cutsom.ShieldEffect;
import com.example.weaponmod.pojo.ShieldData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;


@EventBusSubscriber(modid = "weaponmod")
public class EffectsEvent {

    @SubscribeEvent
    // 事件会在实体即将受到伤害且尚未被减免/处理时触发 —— 可以修改 / 置零伤害
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        // 只在服务器逻辑处理（避免客户端重复逻辑）
        if (entity.level().isClientSide()) return;

        if (!entity.hasEffect(ModEffects.SHIELD)) return; // 没有效果就跳过

        CompoundTag nbt = entity.getPersistentData();
        float shield = nbt.getFloat(ShieldData.NBT_KEY);
        if (shield <= 0f) return;

        float incoming = event.getAmount(); // 可读/写 —— 正是我们需要的
        // 行为按你的要求：不传递多余伤害给生命值（过量也被“吃掉”），并把护盾值归 0
        if (shield >= incoming) {
            nbt.putFloat(ShieldData.NBT_KEY, shield - incoming);
            event.setAmount(0f); // 停止传递伤害（玩家/实体不会受伤）
        } else {
            // 护盾不足但仍然要**阻止所有伤害**（按你例子：5 护盾遭受 10 伤害 => 护盾 0，实体不受伤）
            nbt.putFloat(ShieldData.NBT_KEY, 0f);
            event.setAmount(0f);
        }

        // 调试输出
        entity.sendSystemMessage(
                Component.literal("[护盾测试] 剩余护盾: " + nbt.getFloat(ShieldData.NBT_KEY))
        );

        // 如果护盾被耗尽，可以移除效果（可选）
        if (nbt.getFloat(ShieldData.NBT_KEY) <= 0f) {
            entity.removeEffect(ModEffects.SHIELD);
        }

        // 可选：这里播放粒子/音效或发送数据给客户端以更新 UI
    }
}
