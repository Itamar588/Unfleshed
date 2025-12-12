package com.unfleshed.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final DefaultParticleType BLOOD = Registry.register(
            Registries.PARTICLE_TYPE,
            new Identifier("unfleshed", "blood"),
            FabricParticleTypes.simple()
    );

    public static void registerParticles() {
        // just call this in Unfleshed.java
    }
}
