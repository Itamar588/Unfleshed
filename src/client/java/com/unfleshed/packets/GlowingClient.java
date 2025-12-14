package com.unfleshed.packets;

import com.unfleshed.network.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

import java.util.HashSet;
import java.util.Set;

public class GlowingClient implements ClientModInitializer {
    private static final Set<Integer> glowingEntities = new HashSet<>();
    private static MinecraftClient client;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.GLOW_ENTITY, this::handleGlowPacket);
    }

    private void handleGlowPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readInt();
        client.execute(() -> {
            if (client.world == null) return;
            Entity entity = client.world.getEntityById(entityId);
            if (entity != null) {
                addGlow(entity);
            }
        });
    }

    public static void addGlow(Entity e) {
        if (e != null) {
            glowingEntities.add(e.getId());
            e.setGlowing(true);
            e.setCustomNameVisible(true);
        }
    }

    public static void removeGlow(Entity e) {
        if (e != null) {
            glowingEntities.remove(e.getId());
            e.setGlowing(false);
            e.setCustomNameVisible(false);
        }
    }

    public static boolean shouldGlow(Entity e) {
        return e != null && glowingEntities.contains(e.getId());
    }

    public static void clearAll() {
        if (client != null && client.world != null) {
            for (int id : new HashSet<>(glowingEntities)) {
                Entity e = client.world.getEntityById(id);
                if (e != null) {
                    e.setGlowing(false);
                    e.setCustomNameVisible(false);
                }
            }
        }
        glowingEntities.clear();
    }

    private void tick(MinecraftClient client) {
        // Keep empty for now, can be used for periodic updates if needed
    }
}