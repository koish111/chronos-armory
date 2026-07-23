package com.example.weaponmod.weaponskill;

import com.example.weaponmod.network.ChargeSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChargeManager {
    // player UUID → weapon ID → 当前充能
    private static final Map<UUID, Map<ResourceLocation, Integer>> playerCharges = new HashMap<>();
    // weapon ID → 该武器的最大充能
    private static final Map<ResourceLocation, Integer> maxCharges = new HashMap<>();

    /** 注册一把武器的最大充能值（在武器初始化时调用） */
    public static void registerWeapon(ResourceLocation weaponId, int maxCharge) {
        maxCharges.put(weaponId, maxCharge);
    }

    /** 获取武器最大充能 */
    public static int getMaxCharge(ResourceLocation weaponId) {
        return maxCharges.getOrDefault(weaponId, 0);
    }

    /** 获取玩家对某武器的当前充能 */
    public static int getCharge(Player player, ResourceLocation weaponId) {
        return playerCharges.getOrDefault(player.getUUID(), Map.of()).getOrDefault(weaponId, 0);
    }

    /** 增加对某武器的充能 */
    public static void addCharge(Player player, ResourceLocation weaponId, int amount) {
        int max = getMaxCharge(weaponId);
        if (max <= 0) return; // 未注册的武器不充能
        int newVal = Math.min(
                playerCharges.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
                        .getOrDefault(weaponId, 0) + amount,
                max
        );
        playerCharges.get(player.getUUID()).put(weaponId, newVal);
        syncToClient(player, weaponId);
    }

    /** 尝试消耗某武器的全部充能（必须已满才能消耗），返回是否成功 */
    public static boolean tryConsumeFull(Player player, ResourceLocation weaponId) {
        Map<ResourceLocation, Integer> map = playerCharges.get(player.getUUID());
        if (map == null) return false;
        Integer val = map.get(weaponId);
        int max = getMaxCharge(weaponId);
        if (val == null || val < max) return false;
        map.put(weaponId, 0);
        syncToClient(player, weaponId);
        return true;
    }

    /** 同步某武器的充能值到客户端 */
    private static void syncToClient(Player player, ResourceLocation weaponId) {
        if (player instanceof ServerPlayer sp) {
            int charge = playerCharges
                    .getOrDefault(player.getUUID(), Map.of())
                    .getOrDefault(weaponId, 0);
            int max = getMaxCharge(weaponId);
            PacketDistributor.sendToPlayer(sp, new ChargeSyncPacket(weaponId, charge, max));
        }
    }

    /** 被动充能：所有已注册武器每秒+2，由 ServerTickHandler 每 tick 调用 */
    public static void tickPassive(ServerLevel level) {
        long gameTime = level.getGameTime();
        if (gameTime % 10 != 0) return; // 每10tick+1 = 每秒+2
        for (ServerPlayer player : level.players()) {
            for (ResourceLocation weaponId : maxCharges.keySet()) {
                addCharge(player, weaponId, 1);
            }
        }
    }
}
