package com.unfleshed.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
public class HumanEyesItem extends Item {

    public HumanEyesItem(Settings settings){
        super(settings);
    };

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            System.out.println("HumanEyesItem right-clicked!");
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

}
