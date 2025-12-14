package com.unfleshed.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class GazeParticle extends SpriteBillboardParticle {

    protected GazeParticle(ClientWorld world,
                           double x, double y, double z,
                           double vx, double vy, double vz,
                           SpriteProvider spriteProvider) {
        super(world, x, y, z, vx, vy, vz);

        // Size & lifetime
        this.scale = 0.18f + this.random.nextFloat() * 0.05f;
        this.maxAge = 40 + this.random.nextInt(20);

        // NO COLOR TINT (important)
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;

        // Very low gravity → floaty eyeballs
        this.gravityStrength = 0.02f;

        // Decently strong outward velocity
        this.velocityX = vx * 1.8;
        this.velocityY = vy * 1.2 + 0.02; // slight upward bias
        this.velocityZ = vz * 1.8;

        // Slight air drag so they slow down naturally
        this.velocityMultiplier = 0.92f;

        this.setSprite(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type,
                                       ClientWorld world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new GazeParticle(world, x, y, z, vx, vy, vz, spriteProvider);
        }
    }
}
