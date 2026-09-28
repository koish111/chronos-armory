package com.example.weaponmod.events;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.effects.ModEffects;
import com.example.weaponmod.items.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AntaresRapier（流火之熄）的两个被动技能，均要求武器位于主手堆栈内。
 * <p>被动1·黑焰裁决：对持有任意药水效果的单位伤害 +10%（直接结算，不改无敌帧），造成伤害时附加 2 秒黑焰。
 * <p>被动2·虚无形骸：生命值全满时受到的伤害降低 40%（无冷却）；手持武器时周围 5 格内有
 * 敌对生物（{@link Enemy}）或玩家死亡，使自身与半径 5 格内的友方玩家（同队判定）获得 10 秒无形（内置 15 秒冷却）。
 */
@EventBusSubscriber(modid = WeaponMod.MODID)
public class RapierPassiveHandler {

    private static final float VERDICT_DAMAGE_BONUS = 0.10F;
    private static final int VERDICT_BLACKFIRE_DURATION = 40; // 2秒

    private static final float PHANTOM_DAMAGE_REDUCTION = 0.40F;
    private static final int PHANTOM_UNSEEN_DURATION = 200; // 10秒
    private static final int PHANTOM_UNSEEN_COOLDOWN = 300; // 15秒
    private static final double PHANTOM_UNSEEN_RADIUS = 5.0D;

    /** 击杀授无形的内置冷却按游戏刻记录，沿用仓库 Manager 的 Map<UUID, Data> 静态表模式。 */
    private static final Map<UUID, Long> PHANTOM_UNSEEN_COOLDOWNS = new HashMap<>();

    private static boolean hasRapierInMainHand(Player player) {
        return player.getMainHandItem().is(ModItems.ANTARES_RAPIER.get());
    }

    /** 被动1·黑焰裁决：对持有任意药水效果的单位伤害提升 10%。 */
    @SubscribeEvent
    public static void onVerdictIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasRapierInMainHand(player)) return;

        LivingEntity target = event.getEntity();
        if (target.getActiveEffects().isEmpty()) return;

        event.setAmount(event.getAmount() * (1.0F + VERDICT_DAMAGE_BONUS));
    }

    /** 被动1·黑焰裁决：对目标造成伤害后附加 2 秒黑焰。 */
    @SubscribeEvent
    public static void onVerdictDamageDealt(LivingDamageEvent.Post event) {
        if (event.getNewDamage() <= 0) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasRapierInMainHand(player)) return;

        LivingEntity target = event.getEntity();
        if (target.isAlive()) {
            target.addEffect(new MobEffectInstance(ModEffects.BLACKFIRE, VERDICT_BLACKFIRE_DURATION, 0), player);
        }
    }

    /** 被动2·虚无形骸：生命值全满时受到的伤害降低 40%（无冷却，手持即生效）。 */
    @SubscribeEvent
    public static void onPhantomIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!hasRapierInMainHand(player)) return;
        if (player.getHealth() < player.getMaxHealth()) return;

        event.setAmount(event.getAmount() * (1.0F - PHANTOM_DAMAGE_REDUCTION));
    }

    /** 被动2·虚无形骸：手持武器时周围 5 格内有敌对生物或玩家死亡，自身与友方玩家获得 10 秒无形（内置 15 秒冷却）。 */
    @SubscribeEvent
    public static void onPhantomNearbyDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) return;
        if (!(victim instanceof Enemy) && !(victim instanceof Player)) return;

        for (Player holder : victim.level().getEntitiesOfClass(Player.class,
                victim.getBoundingBox().inflate(PHANTOM_UNSEEN_RADIUS))) {
            // 死亡的是持有者本人时不触发
            if (holder == victim || !holder.isAlive() || !hasRapierInMainHand(holder)) continue;

            long now = holder.level().getGameTime();
            Long lastGrant = PHANTOM_UNSEEN_COOLDOWNS.get(holder.getUUID());
            if (lastGrant != null && now - lastGrant < PHANTOM_UNSEEN_COOLDOWN) continue;
            PHANTOM_UNSEEN_COOLDOWNS.put(holder.getUUID(), now);

            grantUnseen(holder);
        }
    }

    /** 以持有者为中心：自身 + 半径 5 格内友方玩家（原版队伍结盟判定，无队伍时仅自身生效）。 */
    private static void grantUnseen(Player holder) {
        holder.addEffect(new MobEffectInstance(ModEffects.UNSEEN, PHANTOM_UNSEEN_DURATION, 0));
        for (Player other : holder.level().getEntitiesOfClass(Player.class,
                holder.getBoundingBox().inflate(PHANTOM_UNSEEN_RADIUS))) {
            if (other != holder && other.isAlive() && other.isAlliedTo(holder)) {
                other.addEffect(new MobEffectInstance(ModEffects.UNSEEN, PHANTOM_UNSEEN_DURATION, 0));
            }
        }
    }
}
