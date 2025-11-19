package com.unfleshed.application;


import net.minecraft.entity.player.PlayerEntity;

public class ApplicationManager {
    public static void Apply(PlayerEntity player) {
        EyeApplicationManager.Apply(player);
    }
}
