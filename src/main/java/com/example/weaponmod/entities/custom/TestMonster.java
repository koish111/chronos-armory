package com.example.weaponmod.entities.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TestMonster extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean activeAtNight = false;
    private int targetingTimer = 0;
    private int engageTimer = 0;

    private enum BossState {
        DAY_IDLE,      // 白天静止
        TARGETING,     // 索敌动画
        COMBAT_IDLE,   // 战斗待机
        COMBAT_MOVING  // 战斗移动
    }

    private BossState currentState = BossState.DAY_IDLE;
    private BossState previousState = null;

    private final AnimationController<TestMonster> targetingController =
            new AnimationController<>(this, "targeting", 0, this::targetingPredicate);

    private final AnimationController<TestMonster> movementController =
            new AnimationController<>(this, "movement", 2, this::movementPredicate);

    private final AnimationController<TestMonster> idleController =
            new AnimationController<>(this, "idle", 0, this::idlePredicate);

    private static final EntityDataAccessor<Boolean> TARGETING =
            SynchedEntityData.defineId(TestMonster.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> ENGAGED =
            SynchedEntityData.defineId(TestMonster.class, EntityDataSerializers.BOOLEAN);

    public TestMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    // 状态管理方法
    private void setState(BossState newState) {
        if (this.currentState != newState) {
            this.previousState = this.currentState;
            this.currentState = newState;
            this.onStateChanged();
        }
    }

    private void onStateChanged() {
        // 状态变化时的处理逻辑
        switch (currentState) {
            case TARGETING -> {
                this.targetingTimer = 60; // 3秒索敌动画
                this.getNavigation().stop(); // 停止移动
            }
            case COMBAT_IDLE -> {
                // 进入战斗待机状态
            }
        }
    }

    private void updateBossState() {
        boolean isDay = level().isDay();
        Player nearest = level().getNearestPlayer(this, 7.5);
        boolean hasTarget = nearest != null;
        if (isDay) {
            setState(BossState.DAY_IDLE);
            return;
        }
        // 夜晚状态逻辑
        if (currentState == BossState.DAY_IDLE && hasTarget) {
            setState(BossState.TARGETING);
        }
        else if (currentState == BossState.TARGETING) {
            targetingTimer--;
            if (targetingTimer <= 0) {
                setState(BossState.COMBAT_IDLE);
            }
        }
        else if (currentState == BossState.COMBAT_IDLE || currentState == BossState.COMBAT_MOVING) {
            if (!hasTarget) {
                setState(BossState.DAY_IDLE);
            } else {
                // 根据移动状态切换
                boolean isMoving = this.getDeltaMovement().lengthSqr() > 0.001;
                setState(isMoving ? BossState.COMBAT_MOVING : BossState.COMBAT_IDLE);
                // 面向目标
                if (nearest != null) {
                    this.getLookControl().setLookAt(nearest, 30.0F, 30.0F);
                }
            }
        }
    }

    // 动画谓词方法 - 职责分离
    private PlayState targetingPredicate(AnimationState<TestMonster> state) {
        if (currentState != BossState.TARGETING) {
            return PlayState.STOP;
        }
        if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().setAnimation(RawAnimation.begin().then("enter_combat_mode", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }

    private PlayState movementPredicate(AnimationState<TestMonster> state) {
        if (currentState != BossState.COMBAT_MOVING && currentState != BossState.COMBAT_IDLE) {
            return PlayState.STOP;
        }
        // 简化的移动动画逻辑
        if (currentState == BossState.COMBAT_MOVING) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk2"));
        }
        return PlayState.STOP; // 移动控制器不处理待机状态
    }

    private PlayState idlePredicate(AnimationState<TestMonster> state) {
        if (currentState == BossState.DAY_IDLE) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        else if (currentState == BossState.COMBAT_IDLE) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation.model.idle"));
        }
        return PlayState.STOP;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 13.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ARMOR, 15.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0d, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8d));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 0.8f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide()) {
            updateBossState();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGETING, false);
        builder.define(ENGAGED, false);
    }

    @Override
    public boolean isNoAi() {
        return currentState == BossState.DAY_IDLE || currentState == BossState.TARGETING;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return currentState == BossState.DAY_IDLE || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isAggressive() {
        return activeAtNight;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(targetingController);
        controllers.add(movementController);
        controllers.add(idleController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
