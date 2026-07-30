package com.example.weaponmod.weaponskill;

import com.example.weaponmod.mixin.LivingEntityAccessor;
import com.example.weaponmod.sounds.ModSounds;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * 空之刃的激流冲刺。
 * </p>推进方向、自旋姿态和落地抬升来自原版三叉戟激流。原版自旋碰撞会固定使用{@code playerAttack} 伤害源，因此这里让原版自旋只负责姿态，并用相同的扫掠范围自行检测命中，以魔法伤害结算
 */
public final class DashManager {
    private static final Map<UUID, DashData> DASHING_PLAYERS = new HashMap<>();

    private static final double DASH_SPEED = 3.9D;
    private static final int AUTO_SPIN_TICKS = 20;
    private static final int MAX_DASH_TICKS = 12;
    private static final double GROUND_LIFT = 1.1999999D;
    private static final double MIN_MOVEMENT_PER_TICK_SQR = 0.01D * 0.01D;

    private DashManager() {
    }

    public static void startDash(Player player, ItemStack weaponStack, double dashDistance) {
        if (player.level().isClientSide()) {
            return;
        }
        if (dashDistance <= 0.0D) {
            return;
        }

        float yaw = player.getYRot();
        float pitch = player.getXRot();
        float directionX = -Mth.sin(yaw * Mth.DEG_TO_RAD) * Mth.cos(pitch * Mth.DEG_TO_RAD);
        float directionY = -Mth.sin(pitch * Mth.DEG_TO_RAD);
        float directionZ = Mth.cos(yaw * Mth.DEG_TO_RAD) * Mth.cos(pitch * Mth.DEG_TO_RAD);
        float directionLength = Mth.sqrt(
                directionX * directionX + directionY * directionY + directionZ * directionZ
        );
        Vec3 direction = new Vec3(
                directionX / directionLength,
                directionY / directionLength,
                directionZ / directionLength
        );

        player.setDeltaMovement(Vec3.ZERO);
        double initialSpeed = Math.min(DASH_SPEED, dashDistance);
        player.push(
                direction.x * initialSpeed,
                direction.y * initialSpeed,
                direction.z * initialSpeed
        );

        player.startAutoSpinAttack(AUTO_SPIN_TICKS, 0.0F, ItemStack.EMPTY);
        if (player.onGround()) {
            player.move(MoverType.SELF, new Vec3(0.0D, GROUND_LIFT, 0.0D));
        }

        syncMotion(player);

        float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float dashDamage = (attackDamage + 3.0F) * 2.0F;
        DASHING_PLAYERS.put(
                player.getUUID(),
                new DashData(
                        player.level().dimension(),
                        player.position(),
                        player.getBoundingBox(),
                        direction,
                        weaponStack.copy(),
                        dashDamage,
                        dashDistance
                )
        );

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                ModSounds.NULL_BLADE_SOUND.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }

    public static void tick(ServerLevel level) {
        Iterator<Map.Entry<UUID, DashData>> iterator = DASHING_PLAYERS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, DashData> entry = iterator.next();
            DashData data = entry.getValue();
            if (!data.dimension.equals(level.dimension())) {
                continue;
            }

            Player player = level.getPlayerByUUID(entry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }
            if (!player.isAlive()) {
                stopDash(player, iterator, Vec3.ZERO);
                continue;
            }

            AABB currentBox = player.getBoundingBox();
            Vec3 currentPosition = player.position();
            LivingEntity hitTarget = findSpinTarget(level, player, data.previousBox.minmax(currentBox));
            if (hitTarget != null) {
                dealMagicDashDamage(level, player, hitTarget, data);
                stopDash(player, iterator, data.direction.scale(-DASH_SPEED * 0.2D));
                continue;
            }

            boolean movementBlocked = data.hasMovementSample
                    && currentPosition.distanceToSqr(data.previousPosition) < MIN_MOVEMENT_PER_TICK_SQR;
            double traveled = currentPosition.distanceTo(data.startPosition);
            if (traveled >= data.maxDistance
                    || !player.isAutoSpinAttack()
                    || player.horizontalCollision
                    || movementBlocked
                    || --data.remainingTicks <= 0) {
                stopDash(player, iterator, Vec3.ZERO);
                continue;
            }

            double remainingDistance = data.maxDistance - traveled;
            double nextStep = Math.min(DASH_SPEED, remainingDistance);
            Vec3 nextMotion = data.direction.scale(nextStep);
            player.setDeltaMovement(nextMotion);
            player.hurtMarked = true;
            syncMotion(player);
            data.previousBox = currentBox;
            data.previousPosition = currentPosition;
            data.hasMovementSample = true;
        }
    }

    private static LivingEntity findSpinTarget(ServerLevel level, Player player, AABB sweptBox) {
        List<Entity> entities = level.getEntities(
                player,
                sweptBox,
                entity -> entity instanceof LivingEntity livingEntity
                        && livingEntity.isAlive()
                        && livingEntity.isAttackable()
        );
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }
        return null;
    }

    private static void dealMagicDashDamage(
            ServerLevel level,
            Player player,
            LivingEntity target,
            DashData data
    ) {
        DamageSource magicSource = player.damageSources().source(DamageTypes.MAGIC, player);
        float damage = EnchantmentHelper.modifyDamage(
                level,
                data.weaponStack,
                target,
                magicSource,
                data.damage
        );
        if (target.hurt(magicSource, damage)) {
            player.setLastHurtMob(target);
            EnchantmentHelper.doPostAttackEffectsWithItemSource(
                    level,
                    target,
                    magicSource,
                    data.weaponStack
            );
        }
    }

    private static void stopDash(
            Player player,
            Iterator<Map.Entry<UUID, DashData>> iterator,
            Vec3 finalMotion
    ) {
        iterator.remove();
        clearAutoSpinAttack(player);
        player.setDeltaMovement(finalMotion);
        player.hurtMarked = true;
        syncMotion(player);
    }

    private static void clearAutoSpinAttack(Player player) {
        LivingEntityAccessor accessor = (LivingEntityAccessor) player;
        accessor.weaponmod$setAutoSpinAttackTicks(0);
        accessor.weaponmod$setAutoSpinAttackDamage(0.0F);
        accessor.weaponmod$setAutoSpinAttackItemStack(null);
        accessor.weaponmod$setLivingEntityFlag(4, false);
    }

    private static void syncMotion(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    private static final class DashData {
        private final ResourceKey<Level> dimension;
        private final Vec3 startPosition;
        private Vec3 previousPosition;
        private AABB previousBox;
        private final Vec3 direction;
        private final ItemStack weaponStack;
        private final float damage;
        private final double maxDistance;
        private boolean hasMovementSample;
        private int remainingTicks = MAX_DASH_TICKS;

        private DashData(
                ResourceKey<Level> dimension,
                Vec3 startPosition,
                AABB previousBox,
                Vec3 direction,
                ItemStack weaponStack,
                float damage,
                double maxDistance
        ) {
            this.dimension = dimension;
            this.startPosition = startPosition;
            this.previousPosition = startPosition;
            this.previousBox = previousBox;
            this.direction = direction;
            this.weaponStack = weaponStack;
            this.damage = damage;
            this.maxDistance = maxDistance;
        }
    }
}
