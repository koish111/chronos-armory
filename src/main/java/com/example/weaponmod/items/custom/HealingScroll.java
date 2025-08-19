package com.example.weaponmod.items.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class HealingScroll extends Item {
    public HealingScroll(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            player.heal(5.0F);

            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }

            player.getCooldowns().addCooldown(this, 20);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                    0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        } else {
            spawnHealingParticles(level, player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void spawnHealingParticles(Level level, Player player) {
        if (level.isClientSide()) {
            RandomSource random = level.random;

            for (int i = 0; i < 10; i++) {
                double x = player.getX() + (random.nextDouble() - 0.5) * 2.0;
                double y = player.getY() + random.nextDouble() * 2.0;
                double z = player.getZ() + (random.nextDouble() - 0.5) * 2.0;

                level.addParticle(ParticleTypes.HEART, x, y, z, 0, 0, 0);
            }

            for (int i = 0; i < 20; i++) {
                double x = player.getX() + (random.nextDouble() - 0.5) * 2.0;
                double y = player.getY() + random.nextDouble() * 2.0;
                double z = player.getZ() + (random.nextDouble() - 0.5) * 2.0;

                level.addParticle(ParticleTypes.GLOW, x, y, z, 0, 0.1, 0);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.weaponmod.healing_scroll.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
