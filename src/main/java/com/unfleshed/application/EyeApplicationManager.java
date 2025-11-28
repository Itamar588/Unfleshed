package com.unfleshed.application;

import com.unfleshed.Components.MyComponents;
import com.unfleshed.damage.ModDamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;

public class EyeApplicationManager
{
    public static void Apply(PlayerEntity player)
    {
        switch (MyComponents.EYES.get(player).getState()){
            case BLIND -> {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.BLINDNESS, // effect
                        40,                      // duration in ticks (1 second)
                        1,                       // amplifier (level II)
                        false,                   // show particles
                        false                    // show icon
                ));
            }
            case ARCANE -> {
            }//TODO reveal invisible players nearby
        }
    }
}
