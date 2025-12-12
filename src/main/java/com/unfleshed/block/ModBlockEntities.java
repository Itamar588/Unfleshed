package com.unfleshed.block;

import com.unfleshed.Unfleshed;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<SanguisVelumBlockEntity> SANGUIS_VELUM =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    new Identifier(Unfleshed.MOD_ID, "sanguis_velum"),
                    BlockEntityType.Builder.create(SanguisVelumBlockEntity::new, ModBlocks.SANGUIS_VELUM).build(null)
            );

    public static void registerBlockEntities() {
        // Nothing extra needed
    }
}
