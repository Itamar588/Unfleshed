package com.unfleshed.mixin.client;

import com.unfleshed.packets.GlowingClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(T entity, float yaw, float tickDelta, MatrixStack matrices,
                         VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (GlowingClient.shouldGlow(entity)) {
            // Ensure the entity is glowing and name is visible
            entity.setGlowing(true);
            if (entity instanceof LivingEntity) {
                // For living entities, ensure the name is visible for the glow effect to show
                entity.setCustomNameVisible(true);
            }
        }
    }
}