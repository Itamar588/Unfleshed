// src/client/java/com/unfleshed/client/UnfleshedClient.java
package com.unfleshed;

import com.unfleshed.network.ModPackets;
import com.unfleshed.gui.SurgicalKnifeScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class UnfleshedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_SURGICAL_GUI, (client, handler, buf, responseSender) -> {
            client.execute(() -> client.setScreen(new SurgicalKnifeScreen()));
        });
    }
}
