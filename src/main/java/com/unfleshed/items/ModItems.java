package com.unfleshed.items;

import com.unfleshed.Unfleshed;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

public class ModItems {

    public static final Item SURGICAL_KNIFE = new SurgicalKnifeItem(ToolMaterials.IRON, -1, -1.5f,new FabricItemSettings().maxCount(1).maxDamage(250));
    public static final Item HUMAN_EYES = new HumanEyesItem(new FabricItemSettings());
    public static final Item ARCANE_EYES = new ArcaneEyesItem(new FabricItemSettings());
    public  static final Item HEMOCLAST = new HemoclastItem(ToolMaterials.STONE, 2, -2f,new FabricItemSettings().maxCount(1).maxDamage(200));
    public static final Item HEMOCRYSTAL = new HemoCrystalItem(new FabricItemSettings());


    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier(Unfleshed.MOD_ID, "surgical_knife"), SURGICAL_KNIFE);
        Registry.register(Registries.ITEM, new Identifier(Unfleshed.MOD_ID, "human_eyes"), HUMAN_EYES);
        Registry.register(Registries.ITEM, new Identifier(Unfleshed.MOD_ID, "arcane_eyes"), ARCANE_EYES);
        Registry.register(Registries.ITEM, new Identifier(Unfleshed.MOD_ID, "hemoclast"), HEMOCLAST);
        Registry.register(Registries.ITEM, new Identifier(Unfleshed.MOD_ID, "hemocrystal"), HEMOCRYSTAL);

    }
}
