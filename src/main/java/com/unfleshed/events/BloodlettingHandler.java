package com.unfleshed.events;

import com.unfleshed.block.SanguisVelumBlockEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Comparator;

public class BloodlettingHandler {

    public static void init() {
        // Register listener
        BloodlettingEvent.EVENT.register(BloodlettingHandler::onBloodletting);
    }

    private static void onBloodletting(PlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient) return;

        ServerWorld serverWorld = (ServerWorld) world;

        // Search radius
        double RADIUS = 8.0;

        SanguisVelumBlockEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        // Iterate block entities in world
        for (SanguisVelumBlockEntity altar : SanguisVelumBlockEntity.ACTIVE_ALTARS) {

            if (altar.getWorld() != serverWorld) continue;

            BlockPos pos = altar.getPos();
            double dist = pos.getSquaredDistance(player.getBlockPos());

            if (dist < RADIUS * RADIUS && dist < closestDist) {
                closestDist = dist;
                closest = altar;
            }
        }

        if (closest != null) {
            closest.beginRitual(player);
        }
    }
}
