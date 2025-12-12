package com.unfleshed.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class BloodParticle extends SpriteBillboardParticle {

    protected BloodParticle(ClientWorld world, double x, double y, double z,
                            double vx, double vy, double vz,
                            SpriteProvider spriteProvider) {
        super(world, x, y, z, vx, vy, vz);
        this.scale = 0.2f;
        this.maxAge = 20 + this.random.nextInt(10);
        this.red = 0.9f;
        this.green = 0.0f;
        this.blue = 0.0f;
        this.gravityStrength = 1.0f;

        // Critical: assign sprite
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
        public Particle createParticle(DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new BloodParticle(world, x, y, z, vx, vy, vz, spriteProvider);
        }
    }
}
