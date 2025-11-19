package com.unfleshed.Components;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;

public class MyComponentsInitializer implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Attach EyesComponent to all players
        registry.registerFor(PlayerEntity.class, MyComponents.EYES, EyesComponent::new);
    }
}
