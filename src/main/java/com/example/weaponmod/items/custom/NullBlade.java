package com.example.weaponmod.items.custom;

import com.example.weaponmod.items.renderer.NullBladeRenderer;
import com.example.weaponmod.weaponskill.SkillManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class NullBlade extends SwordItem implements GeoItem {
    public NullBlade() {
        super(Tiers.NETHERITE, new SwordItem.Properties().
                attributes(SwordItem.createAttributes(Tiers.NETHERITE, 7, -2.3f)));
    }

    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        Level level = target.level();
        // 临时使无敌帧变为0，造成额外伤害后还原
        int originalInvulnerableTime = target.invulnerableTime;
        target.invulnerableTime = 0;
        target.hurt(target.damageSources().generic(), 3.0f);
        target.invulnerableTime = originalInvulnerableTime;

        // 获取附魔数量
        int enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack).size();

        // 斩杀概率计算：基础8% + 每条附魔1%，最多加到16%
        final float BASE_EXECUTE_CHANCE = 0.08f;
        final int MAX_BONUS_ENCHANTS = 8;

        float bonusChance = Math.min(enchantments, MAX_BONUS_ENCHANTS) * 0.01f;
        float totalChance = BASE_EXECUTE_CHANCE + bonusChance;


        if (!level.isClientSide()) {
            float currentHealth = target.getHealth();

            if (attacker.getRandom().nextFloat() < totalChance) {
                boolean isExecuted = false;

                if (currentHealth < 50.0f) {
                    target.kill();
                    isExecuted = true;
                } else if(currentHealth >= 50.0f && currentHealth < 100.0f) {
                    target.invulnerableTime = 0;
                    target.hurt(target.damageSources().generic(), 25.0f);
                    target.invulnerableTime = originalInvulnerableTime;
                    isExecuted = true;
                } else if (currentHealth >= 100.0f) {
                    float x = currentHealth - 100.0f;
                    float damage = 35.0f + 0.1f * x;
                    target.invulnerableTime = 0;
                    target.hurt(target.damageSources().generic(), damage);
                    target.invulnerableTime = originalInvulnerableTime;
                    isExecuted = true;
                }

                if (isExecuted) {
                    Vec3 pos = target.position();
                    ((ServerLevel) level).sendParticles(
                            ParticleTypes.GLOW,
                            pos.x, pos.y + target.getBbHeight() / 2.0, pos.z,
                            20,
                            0.3, 0.3, 0.3,
                            0.1
                    );

                    level.playSound(
                            null,
                            pos.x, pos.y, pos.z,
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            SoundSource.PLAYERS,
                            1.0f,
                            1.0f
                    );
                }
            }
        }

        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide()) {
            // 获取玩家视线起点和方向
            Vec3 eyePos = player.getEyePosition(1.0f);
            Vec3 lookVec = player.getLookAngle();
            double range = 8.0; // 检测范围

            // 创建射线检测上下文
            ClipContext context = new ClipContext(eyePos, eyePos.add(lookVec.scale(range)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);

            // 先检测方块碰撞
            BlockHitResult blockHit = level.clip(context);
            Vec3 hitPos = blockHit.getLocation();

            // 初始化目标实体为null
            LivingEntity targetEntity = null;

            // 实体检测
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level,
                    player,
                    eyePos,
                    eyePos.add(lookVec.scale(range)),
                    new AABB(eyePos, eyePos.add(lookVec.scale(range))),
                    entity -> entity instanceof LivingEntity && entity != player && entity.isAttackable()
            );

            // 如果检测到可攻击的活体实体
            if (entityHit != null && entityHit.getEntity() instanceof LivingEntity livingEntity) {
                targetEntity = livingEntity;
                hitPos = livingEntity.position(); // 使用实体位置作为技能中心点
            }

            // 启动技能并传入目标实体
            SkillManager.startSkill(player, hitPos, targetEntity);

            // 冷却和反馈
            player.getCooldowns().addCooldown(this, 20 * 10);
            if (targetEntity != null) {
                player.sendSystemMessage(Component.literal("锁定目标: " + targetEntity.getName().getString()));
            } else {
                player.sendSystemMessage(Component.literal("已触发技能"));
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // 获取附魔数量
        int enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack).size();

        final float BASE_EXECUTE_CHANCE = 0.08f;
        final int MAX_BONUS_ENCHANTS = 8;

        float bonusChance = Math.min(enchantments, MAX_BONUS_ENCHANTS) * 0.01f;
        float totalChance = BASE_EXECUTE_CHANCE + bonusChance;

        int chancePercent = Math.round(totalChance * 100);

        tooltipComponents.add(Component.translatable("item.weaponmod.null_katana.tooltip.line1st"));
        tooltipComponents.add(Component.translatable("item.weaponmod.null_katana.tooltip.line2nd"));
        tooltipComponents.add(Component.translatable("item.weaponmod.null_katana.tooltip.line3rd"));
        tooltipComponents.add(Component.translatable("item.weaponmod.null_katana.tooltip.line4th", chancePercent));
        tooltipComponents.add(Component.translatable("\n"));
        tooltipComponents.add(Component.translatable("item.weaponmod.null_katana.tooltip.line5th"));
        tooltipComponents.add(Component.translatable("item.weaponmod.null_katana.tooltip.line6th"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Nullable
    private static LivingEntity findTargetedEntity(Player player, double range) {
        Vec3 start = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = start.add(look.x * range, look.y * range, look.z * range);

        HitResult hitResult = player.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (entityHit.getEntity() instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        // 如果没有精确命中，尝试AABB检测
        AABB searchBox = player.getBoundingBox().expandTowards(look.x * range, look.y * range, look.z * range).inflate(1.0);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                e -> e != player && e.isAlive() && e.isAttackable());

        if (!entities.isEmpty()) {
            // 找到视线方向上最近的实体
            return entities.stream()
                    .min(Comparator.comparingDouble(e -> player.distanceToSqr(e)))
                    .orElse(null);
        }

        return null;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private NullBladeRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new NullBladeRenderer();

                return this.renderer;
            }
        });
    }
}
