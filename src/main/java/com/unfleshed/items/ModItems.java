package com.unfleshed.items;

import com.unfleshed.Unfleshed;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import javax.tools.Tool;

public class ModItems {

    public static final Item SURGICAL_KNIFE = new SurgicalKnifeItem(ToolMaterials.IRON, -1, -1.5f,new FabricItemSettings().maxCount(1));

    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier(Unfleshed.MOD_ID, "surgical_knife"), SURGICAL_KNIFE);
    }
}
