package com.unfleshed.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ModPackets {
    public static final Identifier GLOW_ENTITY = new Identifier("unfleshed", "glow_entity");
    public static final Identifier CLEAR_GLOW = new Identifier("unfleshed", "clear_glow");
    public static final Identifier EYE_PACKET = new Identifier("unfleshed", "eye_packet");
    public static final Identifier GLOW_STATE = new Identifier("unfleshed", "glow_state");
    public static final Identifier OPEN_SURGICAL_AXE_GUI = new Identifier("unfleshed", "open_surgical_axe_gui");
    public static final Identifier OPEN_SURGICAL_SAW_GUI = new Identifier("unfleshed", "open_surgical_saw_gui");
    public static final Identifier OPEN_SURGICAL_KNIFE_GUI = new Identifier("unfleshed", "open_surgical_knife_gui");
    public static final Identifier LASER_LINE = new Identifier("unfleshed", "laser_line");

    // Existing glow methods (unchanged)
    public static void sendGlowPacket(ServerPlayerEntity player, Entity entity, int durationTicks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeInt(durationTicks);
        buf.writeInt(0); // backward compatibility placeholder
        ServerPlayNetworking.send(player, GLOW_ENTITY, buf);
    }

    public static void sendClearGlowPacket(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, CLEAR_GLOW, PacketByteBufs.empty());
    }

    // LASER_LINE packet
    public static void sendLaserLine(World world, Vec3d start, Vec3d end) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeDouble(start.x);
        buf.writeDouble(start.y);
        buf.writeDouble(start.z);
        buf.writeDouble(end.x);
        buf.writeDouble(end.y);
        buf.writeDouble(end.z);

        // Send to all players in the world
        for (PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, LASER_LINE, buf);
            }
        }
    }

    /**
     * Optional: centralized registration if you want
     * a structured way to register S2C packets.
     */
    public static void register() {
        // Example for LASER_LINE S2C (can add client receiver setup elsewhere)
        // Only writes start/end positions; client handles rendering.
        // No other changes to glow or GUI code.
    }
}
