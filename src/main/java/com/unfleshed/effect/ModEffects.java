package com.unfleshed.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEffects {
    public static final StatusEffect GAZE = new GazeEffect(); // no-arg constructor

    public static void registerEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("unfleshed", "gaze"), GAZE);
    }
}
