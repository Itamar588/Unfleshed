package com.unfleshed.events;

import com.unfleshed.block.SanguisVelumBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;


public class BloodlettingHandler {

    /** Register event handler */
    public static void init() {
        BloodlettingEvent.EVENT.register(BloodlettingHandler::onBloodletting);
        System.out.println("[BloodlettingHandler] Initialized.");
    }

    /**
     * Called when player performs a bloodletting (hemoclast right-click)
     */
    private static void onBloodletting(PlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient) return;

        ServerWorld serverWorld = (ServerWorld) world;
        double RADIUS = 8.0;

        SanguisVelumBlockEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        System.out.println("[Bloodletting] Player " + player.getName().getString() +
                " performed bloodletting at " + player.getBlockPos());

        // Loop through all active altars
        for (SanguisVelumBlockEntity altar : SanguisVelumBlockEntity.ACTIVE_ALTARS) {
            if (altar.getWorld() != serverWorld) continue;

            BlockPos pos = altar.getPos();
            double dist = pos.getSquaredDistance(player.getBlockPos());

            System.out.println("[Bloodletting] Found altar at " + pos + ", distance² = " + dist);

            if (dist < RADIUS * RADIUS && dist < closestDist) {
                closestDist = dist;
                closest = altar;
            }
        }

        if (closest != null) {
            System.out.println("[Bloodletting] DORITUAL AT " + closest.getPos());
            closest.beginRitual(player);
        } else {
            System.out.println("[Bloodletting] No altar in range.");
        }
    }
}
