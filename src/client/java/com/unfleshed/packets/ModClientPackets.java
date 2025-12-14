package com.unfleshed.packets;

import com.unfleshed.network.ModPackets;
import com.unfleshed.packets.GlowingClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ModClientPackets {

    public static void registerReceivers() {
        // Keep the old handlers for backward compatibility
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.GLOW_ENTITY, (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            int duration = buf.readInt();
            buf.readInt(); // Read and discard color

            client.execute(() -> {
                var world = client.world;
                if (world == null) return;
                var e = world.getEntityById(entityId);
                if (e != null) {
                    GlowingClient.addGlow(e);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModPackets.CLEAR_GLOW, (client, handler, buf, responseSender) -> {
            client.execute(GlowingClient::clearAll);
        });
        
        // New handler for GLOW_STATE packet
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.GLOW_STATE, (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            boolean shouldGlow = buf.readBoolean();
            
            client.execute(() -> {
                var world = client.world;
                if (world == null) return;
                var e = world.getEntityById(entityId);
                if (e != null) {
                    e.setGlowing(shouldGlow);
                    e.setCustomNameVisible(shouldGlow);
                }
            });
        });
    }
    
    public static void sendGlowState(int entityId, boolean shouldGlow) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeBoolean(shouldGlow);
        ClientPlayNetworking.send(ModPackets.GLOW_STATE, buf);
    }
}
