package com.example.weaponmod.network;

import com.example.weaponmod.items.custom.NullBlade;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NullBladeSkillPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<NullBladeSkillPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("weaponmod", "null_blade_skill"));

    public static final StreamCodec<FriendlyByteBuf, NullBladeSkillPacket> CODEC =
            StreamCodec.unit(new NullBladeSkillPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                NullBlade.activateSkill(serverPlayer);
            }
        });
    }
}
