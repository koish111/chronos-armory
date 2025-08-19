package com.example.weaponmod.items.custom;

import com.example.weaponmod.entities.custom.BlackFireBallEntity;
import com.example.weaponmod.weaponskill.SkillManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;


public class TestSword extends SwordItem {
    public TestSword() {
        super(Tiers.NETHERITE, new SwordItem.Properties().
                attributes(SwordItem.createAttributes(Tiers.NETHERITE, 7, -2.3f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide) {
            float yaw = player.getYRot();

            for (int i = -1; i <= 1; i++) {
                BlackFireBallEntity blackFireBall = new BlackFireBallEntity(level, player);
                float angleOffset = i * 15.0f;
                blackFireBall.shootFromRotation(player, player.getXRot(), yaw + angleOffset, 0.0f, 1.0f, 0.5f);
                level.addFreshEntity(blackFireBall);
            }

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BLAZE_SHOOT,
                    SoundSource.PLAYERS,
                    1.0f, 1.0f
            );

            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResultHolder.success(stack);
    }
}
