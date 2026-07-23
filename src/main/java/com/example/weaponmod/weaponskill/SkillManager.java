package com.example.weaponmod.weaponskill;

import com.example.weaponmod.particles.ModParticles;
import com.example.weaponmod.pojo.NullBladeParticleOption;
import com.example.weaponmod.pojo.SkillData;
import com.example.weaponmod.sounds.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class SkillManager {
    private static final Map<UUID, SkillData> activeSkills = new HashMap<>();

    // 开始技能
    public static void startSkill(Player player, Vec3 center) {
        if (!player.level().isClientSide && player instanceof ServerPlayer) {
            activeSkills.put(player.getUUID(), new SkillData(center, player.level().getGameTime()));
        }
    }

    public static void startSkill(Player player, Vec3 center, LivingEntity target) {
        if (!player.level().isClientSide && player instanceof ServerPlayer) {
            activeSkills.put(player.getUUID(), new SkillData(center, player.level().getGameTime(), target));
        }
    }

    public static void tick(ServerLevel level) {
        long currentTime = level.getGameTime();

        Iterator<Map.Entry<UUID, SkillData>> iterator = activeSkills.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, SkillData> entry = iterator.next();
            UUID playerId = entry.getKey();
            SkillData data = entry.getValue();

            if (data.timesDone >= 6) {
                iterator.remove();
                continue;
            }

            Player player = level.getPlayerByUUID(playerId);
            if (player == null) continue;

            if (!player.level().dimension().equals(level.dimension())) continue;

            // 依照技能中的伤害间隔（每5 tick）触发一次
            if (currentTime - data.lastTriggerTime >= 5) {
                data.lastTriggerTime = currentTime;
                data.timesDone++;

                // 1. 在半径 4.0 的圆内随机生成一个位置角度
                double spawnAngle = level.random.nextDouble() * 2 * Math.PI;
                // 2. 随机分布在圆盘内（使用 sqrt 保证均匀分布，或者直接 random 保证靠近圆心多一点）
                double dist = Math.sqrt(level.random.nextDouble()) * 4.0;

                double offsetX = Math.cos(spawnAngle) * dist;
                double offsetZ = Math.sin(spawnAngle) * dist;
                double particleY = data.center.y + 0.2 + level.random.nextDouble() * 0.5; // 稍微浮动高度

                // 3. 计算径向旋转角度
                float baseRoll = (float) Math.toDegrees(-spawnAngle);
                float finalRoll = baseRoll + (level.random.nextFloat() * 40f - 20f);

                // 4. 发送粒子
                NullBladeParticleOption particleOptions = new NullBladeParticleOption(finalRoll);

                level.sendParticles(
                        particleOptions,
                        data.center.x + offsetX, particleY, data.center.z + offsetZ,
                        0,      // count=0 此处无所谓，因为 roll 已由 options 承载
                        0.0, 0.0, 0.0,  // dx/dy/dz 不再用于传递角度，全部清零
                        0.0     // speed 也清零，避免粒子产生意外位移
                );

                // --- 伤害与效果逻辑 ---

                if (data.timesDone == 1 && data.target != null && data.target.isAlive()) {
                    executeTarget(player, data.target);
                }

                AABB area = new AABB(
                        data.center.x - 4.0, data.center.y - 1.0, data.center.z - 4.0,
                        data.center.x + 4.0, data.center.y + 2.0, data.center.z + 4.0
                );

                List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, area,
                        e -> e != player && e.isAlive() && e.isAttackable());

                for (LivingEntity enemy : enemies) {
                    int originTime = enemy.invulnerableTime;
                    enemy.invulnerableTime = 0;
                    enemy.hurt(enemy.damageSources().playerAttack(player), 12.0f);
                    enemy.invulnerableTime = originTime;
                }

                // 每一段伤害生成时，绘制一次圆环强调范围
                drawParticleCircle(level, data.center, 4.0, 60); // 降低点数减少卡顿

                level.playSound(null, data.center.x, data.center.y, data.center.z,
                        ModSounds.NULL_BLADE_SOUND.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    // 执行斩杀效果
    private static void executeTarget(Player player, LivingEntity target) {
        float currentHealth = target.getHealth();
        boolean isExecuted = false;
        int originalInvulnerableTime = target.invulnerableTime;

        if (currentHealth < 50.0f) {
            target.kill();
            isExecuted = true;
        }
        else if (currentHealth >= 50.0f && currentHealth < 100.0f) {
            target.invulnerableTime = 0;
            target.hurt(target.damageSources().playerAttack(player), 25.0f);
            target.invulnerableTime = originalInvulnerableTime;
            isExecuted = true;
        }
        else if (currentHealth >= 100.0f) {
            float x = currentHealth - 100.0f;
            float damage = 35.0f + 0.1f * x;
            target.invulnerableTime = 0;
            target.hurt(target.damageSources().playerAttack(player), damage);
            target.invulnerableTime = originalInvulnerableTime;
            isExecuted = true;
        }

        if (isExecuted) {
            Vec3 pos = target.position();
            ((ServerLevel) player.level()).sendParticles(
                    ParticleTypes.GLOW,
                    pos.x, pos.y + target.getBbHeight() / 2.0, pos.z,
                    20,
                    0.3, 0.3, 0.3,
                    0.1
            );

            player.level().playSound(
                    null,
                    pos.x, pos.y, pos.z,
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );
        }
    }

    private static void drawParticleCircle(ServerLevel level, Vec3 center, double radius, int points) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y;

            level.sendParticles(ParticleTypes.END_ROD, x, y + 0.1, z, 1, 0, 0, 0, 0);
        }
    }
}
