package com.unfleshed.effect;

import com.unfleshed.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class GazeEffect extends StatusEffect {

    public GazeEffect() {
        super(StatusEffectCategory.HARMFUL, 0x990000);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient) return;

        // Level 1 = 15 ticks, Level 10 = 3 ticks
        int delay = Math.max(3, 15 - amplifier * 2);

        if (entity.age % delay != 0) return;

        ServerWorld world = (ServerWorld) entity.getWorld();

        world.spawnParticles(
                ModParticles.GAZE,
                entity.getX(),
                entity.getY() + 2.0,
                entity.getZ(),
                1,
                0.5, 0.5, 0.5,
                0.05
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // run every tick, we gate manually
    }
}
