package com.unfleshed;

import com.unfleshed.items.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class UnfleshedItemGroups {

    public static final ItemGroup UNFLESHED_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(Unfleshed.MOD_ID, "unfleshed"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.SURGICAL_KNIFE)) // icon in creative menu
                    .displayName(Text.literal("Unfleshed"))
                    .entries((context, entries) -> {
                        entries.add(ModItems.SURGICAL_KNIFE); // items go here
                    })
                    .build()
    );

    public static void registerItemGroups() {
        // This just forces the static field to load.
        System.out.println("Registering item groups for " + Unfleshed.MOD_ID);
    }
}
