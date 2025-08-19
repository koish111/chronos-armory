package com.example.weaponmod.entities.custom;

import com.example.weaponmod.entities.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class BlackFireBallEntity extends ThrowableItemProjectile implements GeoEntity {
    private static final double TRACKING_RANGE = 10.0;
    private static final double TRACKING_ANGLE = 90.0;
    private static final double TRACKING_SPEED = 0.2;

    private LivingEntity target;
    private int age = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BlackFireBallEntity(EntityType<? extends BlackFireBallEntity> type, Level level) {
        super(type, level);
    }

    public BlackFireBallEntity(Level level, LivingEntity shooter) {
        super(ModEntities.BLACK_FIRE_BALL.get(), shooter, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }

    @Override
    public void tick() {
        super.tick();
        age++;

        if (!this.level().isClientSide && age % 5 == 0) {
            findTarget();
        }
        if (target != null && target.isAlive()) {
            adjustDirectionToTarget();
        }
    }

    private void findTarget() {
        List<LivingEntity> entities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(TRACKING_RANGE),
                entity -> entity != this.getOwner() && entity.isAlive() && !entity.isAlliedTo(this.getOwner())
        );

        if (!entities.isEmpty()) {
            entities.sort(Comparator.comparingDouble(this::distanceToSqr));
            this.target = entities.get(0);
        }
    }

    private void adjustDirectionToTarget() {
        Vec3 currentPos = this.position();
        Vec3 targetPos = target.position();
        Vec3 currentMotion = this.getDeltaMovement();
        Vec3 currentDir = currentMotion.normalize();
        Vec3 toTarget = targetPos.subtract(currentPos).normalize();
        double angle = Math.sqrt(currentDir.dot(toTarget)) * (180.0 / Math.PI);
        if (angle <= TRACKING_ANGLE) {
            Vec3 newDir = currentDir.scale(1.0 - TRACKING_SPEED).add(toTarget.scale(TRACKING_SPEED)).normalize();
            double speed = currentMotion.length();
            this.setDeltaMovement(newDir.scale(speed));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0f, false, Level.ExplosionInteraction.NONE);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            if (result.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.hurt(this.damageSources().thrown(this, this.getOwner()), 10.0f);
                livingEntity.setRemainingFireTicks(20 * 10);
            }
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
