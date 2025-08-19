package com.example.weaponmod.pojo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SkillData {
    public final Vec3 center;
    public long lastTriggerTime;
    public int timesDone = 0;
    @Nullable
    public final LivingEntity target;

    public SkillData(Vec3 center, long startTime) {
        this.center = center;
        this.lastTriggerTime = startTime;
        this.target = null;
    }

    public SkillData(Vec3 center, long startTime, @Nullable LivingEntity target) {
        this.center = center;
        this.lastTriggerTime = startTime;
        this.target = target;
    }
}
