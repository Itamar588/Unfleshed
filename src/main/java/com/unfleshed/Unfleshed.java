package com.unfleshed;

import com.unfleshed.block.ModBlocks;
import com.unfleshed.block.SanguisVelumBlockEntity;
import com.unfleshed.events.BloodlettingHandler;
import com.unfleshed.events.PlayerCloneEvents;
import com.unfleshed.items.ModItems;
import com.unfleshed.network.EyePacket;
import com.unfleshed.particle.ModParticles;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.unfleshed.application.ApplicationManager;


public class Unfleshed implements ModInitializer {
	public static final String MOD_ID = "unfleshed";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModParticles.registerParticles();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) ApplicationManager.Apply(player);
        });
        ModBlocks.registerModBlocks();
        PlayerCloneEvents.register();
        ModItems.registerItems();
        UnfleshedItemGroups.registerItemGroups();
		LOGGER.info("Hello Fabric world!");
        EyePacket.register();
        BloodlettingHandler.init();
	}
}