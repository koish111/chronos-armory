package com.example.weaponmod.entities.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NullBladeDash extends Entity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NullBladeDash(EntityType<? extends NullBladeDash> type, Level level) {
        super(type, level);
        this.noPhysics = true; // 无物理
    }

    // ======================
    // 生命周期（1秒 = 20tick）
    // ======================
    @Override
    public void tick() {
        super.tick();

        if (this.tickCount >= 20) {
            this.discard(); // 自动销毁
        }
    }

    // ======================
    // 无碰撞
    // ======================
    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return super.isPushable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith();
    }

    // ======================
    // 无重力
    // ======================
    @Override
    public boolean isNoGravity() {
        return true;
    }

    // ======================
    // GeckoLib 动画
    // ======================
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", 0, state ->
                        state.setAndContinue(
                                RawAnimation.begin().thenPlay("disa")
                        )
                )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ======================
    // 必要的空实现（1.21）
    // ======================
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}

