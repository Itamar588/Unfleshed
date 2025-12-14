package com.unfleshed.mixin.client;

import com.unfleshed.packets.GlowingClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;
    
    private static final TrackedData<Byte> FLAGS;
    
    static {
        try {
            Field flagsField = Entity.class.getDeclaredField("FLAGS");
            flagsField.setAccessible(true);
            FLAGS = (TrackedData<Byte>) flagsField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access Entity.FLAGS field", e);
        }
    }

    @Inject(method = "startTracking", at = @At("HEAD"))
    private void onStartTracking(ServerPlayerEntity player, CallbackInfo ci) {
        if (GlowingClient.shouldGlow(this.entity)) {
            setGlowingFlag();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (GlowingClient.shouldGlow(this.entity)) {
            setGlowingFlag();
        }
    }

    private void setGlowingFlag() {
        DataTracker tracker = this.entity.getDataTracker();
        byte flags = tracker.get(FLAGS);
        if ((flags & 0x40) == 0) {  // 0x40 is the GLOWING flag
            tracker.set(FLAGS, (byte)(flags | 0x40));
            this.entity.setCustomNameVisible(true);
        }
    }
}
