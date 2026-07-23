package com.example.weaponmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record ChargeSyncPacket(ResourceLocation weaponId, int charge, int maxCharge) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChargeSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("weaponmod", "charge_sync"));

    public static final StreamCodec<FriendlyByteBuf, ChargeSyncPacket> CODEC =
            StreamCodec.ofMember(ChargeSyncPacket::write, ChargeSyncPacket::read);

    // 客户端存储的武器充能数据：weaponId → 充能值
    public static final Map<ResourceLocation, Integer> clientCharges = new HashMap<>();
    public static final Map<ResourceLocation, Integer> clientMaxCharges = new HashMap<>();

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(weaponId);
        buf.writeInt(charge);
        buf.writeInt(maxCharge);
    }

    public static ChargeSyncPacket read(FriendlyByteBuf buf) {
        return new ChargeSyncPacket(
                buf.readResourceLocation(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            clientCharges.put(weaponId, charge);
            clientMaxCharges.put(weaponId, maxCharge);
        });
    }
}
