// src/client/java/com/unfleshed/client/UnfleshedClient.java
package com.unfleshed;

import com.unfleshed.block.ModBlockEntities;
import com.unfleshed.block.ModBlocks;
import com.unfleshed.gui.ArcaneHUD;
import com.unfleshed.network.ModPackets;
import com.unfleshed.gui.SurgicalKnifeScreen;

import com.unfleshed.particle.BloodParticle;
import com.unfleshed.particle.ModParticles;
import com.unfleshed.renderer.SanguisVelumBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry; // FIXED IMPORT
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;

public class UnfleshedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLOOD, BloodParticle.Factory::new);

        // Set the Velum block to translucent render layer
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SANGUIS_VELUM, RenderLayer.getTranslucent());

        // Register network receivers
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_SURGICAL_KNIFE_GUI, (client, handler, buf, responseSender) -> {
            client.execute(() -> client.setScreen(new SurgicalKnifeScreen()));
        });

        // Register Arcane HUD
        ArcaneHUD.register();

        // Register block entity renderer (client-side only)
        BlockEntityRendererRegistry.register(
                ModBlockEntities.SANGUIS_VELUM,
                ctx -> new SanguisVelumBlockEntityRenderer(ctx)
        );
    }
}
