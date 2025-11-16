package com.unfleshed;

import com.unfleshed.items.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Unfleshed implements ModInitializer {
	public static final String MOD_ID = "unfleshed";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModItems.registerItems();
        UnfleshedItemGroups.registerItemGroups();
		LOGGER.info("Hello Fabric world!");
	}
}