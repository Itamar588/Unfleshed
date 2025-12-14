package com.unfleshed.material;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class FleshToolMaterial implements ToolMaterial {

    public static final FleshToolMaterial INSTANCE = new FleshToolMaterial();

    private FleshToolMaterial() {}

    @Override
    public int getDurability() {
        return 4096; // Insane durability (twice netherite)
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 10.0f; // Irrelevant for weapons, still good
    }

    @Override
    public float getAttackDamage() {
        return 6.0f; // BASE damage (Netherite is 4.0)
    }

    @Override
    public int getMiningLevel() {
        return 4; // Netherite tier
    }

    @Override
    public int getEnchantability() {
        return 30; // Extremely enchantable (gold is 25)
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
        // Intentionally empty: repaired via rituals, not an anvil
    }
}
