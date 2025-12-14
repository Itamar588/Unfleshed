package com.unfleshed.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class ModPackets {
    public static final Identifier GLOW_ENTITY = new Identifier("unfleshed", "glow_entity");
    public static final Identifier CLEAR_GLOW = new Identifier("unfleshed", "clear_glow");
    public static final Identifier EYE_PACKET = new Identifier("unfleshed", "eye_packet");
    public static final Identifier GLOW_STATE = new Identifier("unfleshed", "glow_state");
    public static final Identifier OPEN_SURGICAL_AXE_GUI = new Identifier("unfleshed", "open_surgical_axe_gui");
    public static final Identifier OPEN_SURGICAL_SAW_GUI = new Identifier("unfleshed", "open_surgical_saw_gui");
    public static final Identifier OPEN_SURGICAL_KNIFE_GUI = new Identifier("unfleshed", "open_surgical_knife_gui");

    // send glow packet to a player
    public static void sendGlowPacket(ServerPlayerEntity player, Entity entity, int durationTicks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeInt(durationTicks);
        // No longer sending color, but keeping the same packet structure for compatibility
        // with existing clients that might still expect this value
        buf.writeInt(0); // Placeholder for backward compatibility
        ServerPlayNetworking.send(player, GLOW_ENTITY, buf);
    }

    // send clear all glow packet to a player
    public static void sendClearGlowPacket(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, CLEAR_GLOW, PacketByteBufs.empty());
    }
}
