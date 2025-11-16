package com.unfleshed.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.world.World;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.player.PlayerEntity;

public class SurgicalKnifeItem extends SwordItem {

    public SurgicalKnifeItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    // Right-click behavior
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) { // only run on server side
            System.out.println("Surgical Knife used by " + user.getName().getString());
        }

        // Return success so the animation/hand swing works
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
