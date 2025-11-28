package com.unfleshed.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface BloodlettingEvent {
    Event<BloodlettingEvent> EVENT = EventFactory.createArrayBacked(BloodlettingEvent.class,
            (listeners) -> (PlayerEntity player) -> {
                for (BloodlettingEvent event : listeners) {
                    event.onBloodletting(player);
                }
            }
    );

    void onBloodletting(PlayerEntity player);
}
