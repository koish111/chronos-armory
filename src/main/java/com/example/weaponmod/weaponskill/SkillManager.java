package com.example.weaponmod.weaponskill;

import com.example.weaponmod.pojo.SkillData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class SkillManager {
    private static final Map<UUID, SkillData> activeSkills = new HashMap<>();

    // 开始技能
    public static void startSkill(Player player, Vec3 center) {
        if (!player.level().isClientSide && player instanceof ServerPlayer) {
            activeSkills.put(player.getUUID(), new SkillData(center, player.level().getGameTime()));
        }
    }

    public static void startSkill(Player player, Vec3 center, @Nullable LivingEntity target) {
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

            // 保证只在技能起始维度处理
            if (!player.level().dimension().equals(level.dimension())) continue;

            if (currentTime - data.lastTriggerTime >= 5) {
                data.lastTriggerTime = currentTime;
                data.timesDone++;

                // 第一次触发时对瞄准的目标执行斩杀效果
                if (data.timesDone == 1 && data.target != null && data.target.isAlive()) {
                    executeTarget(player, data.target);
                }

                // 原有区域伤害逻辑
                AABB area = new AABB(
                        data.center.x - 4.0, data.center.y - 4.0, data.center.z - 4.0,
                        data.center.x + 4.0, data.center.y + 4.0, data.center.z + 4.0
                );

                List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, area,
                        e -> e != player && e.isAlive() && e.isAttackable());

                for (LivingEntity enemy : enemies) {
                    int originTime = enemy.invulnerableTime;
                    enemy.invulnerableTime = 0;
                    enemy.hurt(enemy.damageSources().playerAttack(player), 12.0f);
                    enemy.invulnerableTime = originTime;
                }

                // 绘制粒子圆
                drawParticleCircle(level, data.center, 4.0, 120);
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
