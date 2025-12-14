package com.unfleshed.misc;

import com.unfleshed.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.entity.Entity;

public class GlowUtils {

    public static void sendGlow(ServerPlayerEntity player, Entity target, int durationTicks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(target.getId());
        buf.writeInt(durationTicks);

        ServerPlayNetworking.send(player, ModPackets.GLOW_ENTITY, buf);
    }
}
