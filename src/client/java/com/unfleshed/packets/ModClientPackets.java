package com.unfleshed.packets;

import com.unfleshed.network.ModPackets;
import com.unfleshed.packets.GlowingClient;
import com.unfleshed.renderer.OculonLaserRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class ModClientPackets {

    public static void registerReceivers() {
        // Existing handlers
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

        ClientPlayNetworking.registerGlobalReceiver(ModPackets.LASER_LINE, (client, handler, buf, responseSender) -> {
            double startX = buf.readDouble();
            double startY = buf.readDouble();
            double startZ = buf.readDouble();
            double endX = buf.readDouble();
            double endY = buf.readDouble();
            double endZ = buf.readDouble();

            client.execute(() -> {
                OculonLaserRenderer.addLaserBeam(
                        new Vec3d(startX, startY, startZ),
                        new Vec3d(endX, endY, endZ)
                );
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
