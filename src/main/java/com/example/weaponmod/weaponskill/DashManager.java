package com.example.weaponmod.weaponskill;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * 仿激流附魔的冲刺实现：
 * 1. startDash: setDeltaMovement 设水平速度 + startAutoSpinAttack 激活碰撞伤害
 * 2. tick: 只检查距离/时间判断是否停止，碰撞检测由原版 autoSpinAttack 引擎处理
 */
public class DashManager {
    private static final Map<UUID, DashData> dashingPlayers = new HashMap<>();

    private static final double SPEED = 3.9;
    private static final double MAX_DISTANCE = 16.0;
    private static final int MAX_TICKS = 12;

    public static void startDash(Player player, ItemStack weaponStack) {
        if (player.level().isClientSide) return;

        float yaw = player.getYRot();
        double dirX = -Math.sin(Math.toRadians(yaw));
        double dirZ = Math.cos(Math.toRadians(yaw));

        // 同激流：速度驱动移动
        Vec3 vel = new Vec3(dirX * SPEED, 0, dirZ * SPEED);
        player.setDeltaMovement(vel);
        if (player instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), vel));
        }
        player.hurtMarked = true;

        // 同激流：启动自旋攻击（原版 aiStep 自动处理每 tick 实体碰撞和伤害）
        // 伤害 = (武器当前攻击力 + 3) × 2
        float baseDmg = (float) player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        float spinDmg = (baseDmg + 3.0f) * 2.0f;
        player.startAutoSpinAttack(20, spinDmg, weaponStack);

        dashingPlayers.put(player.getUUID(), new DashData(player.position(), new Vec3(dirX, 0, dirZ), MAX_TICKS));
    }

    public static void tick(ServerLevel level) {
        Iterator<Map.Entry<UUID, DashData>> iterator = dashingPlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, DashData> entry = iterator.next();
            DashData data = entry.getValue();

            Player player = level.getPlayerByUUID(entry.getKey());
            if (player == null || !player.isAlive()) { iterator.remove(); continue; }

            // 检查距离和超时
            double traveled = player.position().distanceTo(data.startPos);
            if (traveled >= MAX_DISTANCE || --data.remainingTicks <= 0) {
                stopDash(player, iterator);
                continue;
            }

            // 续速度
            Vec3 vel = new Vec3(data.direction.x * SPEED, player.getDeltaMovement().y, data.direction.z * SPEED);
            player.setDeltaMovement(vel);
            if (player instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), vel));
            }
        }
    }

    private static void stopDash(Player player, Iterator<Map.Entry<UUID, DashData>> iterator) {
        iterator.remove();
        player.setDeltaMovement(Vec3.ZERO);
        if (player instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), Vec3.ZERO));
            // 清除自旋攻击状态
            player.startAutoSpinAttack(0, 0, ItemStack.EMPTY);
        }
    }

    private static class DashData {
        final Vec3 startPos;
        final Vec3 direction;
        int remainingTicks;
        DashData(Vec3 startPos, Vec3 direction, int remainingTicks) {
            this.startPos = startPos;
            this.direction = direction;
            this.remainingTicks = remainingTicks;
        }
    }
}
