package com.example.weaponmod.events;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.attachments.ModAttachments;
import com.example.weaponmod.effects.ModEffects;
import com.example.weaponmod.effects.cutsom.DissolveEffect;
import com.example.weaponmod.effects.cutsom.UnseenEffect;
import com.example.weaponmod.pojo.ShieldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber(modid = WeaponMod.MODID)
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
                Component.translatable("message.weaponmod.shield.remaining", nbt.getFloat(ShieldData.NBT_KEY))
        );

        // 如果护盾被耗尽，可以移除效果（可选）
        if (nbt.getFloat(ShieldData.NBT_KEY) <= 0f) {
            entity.removeEffect(ModEffects.SHIELD);
        }

        // 可选：这里播放粒子/音效或发送数据给客户端以更新 UI
    }

    /** 溶解：每次受到伤害时额外受到 3 点伤害（× 等级系数）。 */
    @SubscribeEvent
    public static void onDissolveExtraDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity instanceof Player player && player.isCreative()) return;

        MobEffectInstance instance = entity.getEffect(ModEffects.DISSOLVE);
        if (instance == null) return;

        event.setAmount(event.getAmount() + DissolveEffect.EXTRA_DAMAGE_PER_HIT * (instance.getAmplifier() + 1));
    }

    /** 黑焰与溶解对创造模式玩家无效：效果直接不会被应用。 */
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (!(entity instanceof Player player) || !player.isCreative()) return;

        MobEffectInstance instance = event.getEffectInstance();
        if (instance != null && (instance.is(ModEffects.BLACKFIRE) || instance.is(ModEffects.DISSOLVE))) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    /** 生存期间带溶解切到创造：立即移除生命上限削减；切回生存且效果仍在则重新套用。 */
    @SubscribeEvent
    public static void onChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!player.hasEffect(ModEffects.DISSOLVE)) return;

        boolean creative = event.getNewGameMode().isCreative();
        DissolveEffect.applyOrRemoveModifier(player, !creative);
    }

    /** 无形：持续期间不会被任何生物选定为目标。 */
    @SubscribeEvent
    public static void onUnseenTargetChange(LivingChangeTargetEvent event) {
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();
        if (newTarget != null && newTarget.hasEffect(ModEffects.UNSEEN)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        if (entity.level().isClientSide() || instance == null) return;

        if (instance.is(ModEffects.DISSOLVE)) {
            DissolveEffect.applyOrRemoveModifier(entity, true);
        } else if (instance.is(ModEffects.UNSEEN)) {
            UnseenEffect.clearAggro(entity);
            setUnseenPvpInvisible(entity, isPvpAllowed(entity) && entity.hasEffect(ModEffects.UNSEEN));
        }
    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        cleanupEffect(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        cleanupEffect(event.getEntity(), event.getEffectInstance());
    }

    private static void cleanupEffect(LivingEntity entity, MobEffectInstance instance) {
        if (entity.level().isClientSide() || instance == null) return;

        if (instance.is(ModEffects.DISSOLVE)) {
            DissolveEffect.applyOrRemoveModifier(entity, false);
        } else if (instance.is(ModEffects.UNSEEN)) {
            setUnseenPvpInvisible(entity, false);
        }
    }

    /** 进世界时同步一次状态：清理/补挂溶解修饰符（创造模式不挂），按 PvP 状态重建隐形标记。 */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        DissolveEffect.applyOrRemoveModifier(player, !player.isCreative() && player.hasEffect(ModEffects.DISSOLVE));
        setUnseenPvpInvisible(player, isPvpAllowed(player) && player.hasEffect(ModEffects.UNSEEN));
    }

    private static boolean isPvpAllowed(LivingEntity entity) {
        MinecraftServer server = entity.getServer();
        return server != null && server.isPvpAllowed();
    }

    private static void setUnseenPvpInvisible(LivingEntity entity, boolean invisible) {
        if (entity instanceof ServerPlayer player) {
            player.setData(ModAttachments.UNSEEN_PVP_INVISIBLE, invisible);
            player.syncData(ModAttachments.UNSEEN_PVP_INVISIBLE);
        }
    }
}
