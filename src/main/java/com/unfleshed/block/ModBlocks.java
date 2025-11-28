package com.unfleshed.block;

import com.unfleshed.Unfleshed;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block SANGUIS_VELUM = registerBlock(
            "sanguis_velum",
            new SanguisVelumBlock(FabricBlockSettings.create().luminance(2))
    );

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(Unfleshed.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(Unfleshed.MOD_ID, name), block);
    }

    public static void registerModBlocks() {
        // All registration handled in static fields
    }
}
