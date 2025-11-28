package com.unfleshed.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import com.unfleshed.Components.MyComponents;
import com.unfleshed.Components.EyesComponent;

public class ArcaneEyesItem extends Item {

    public ArcaneEyesItem(Settings settings) {
        super(settings);
    }

    ;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            EyesComponent eyes = MyComponents.EYES.get(user);
            if (eyes.getState() == EyesComponent.EyeState.BLIND) {
                boolean removed = false;
                for (int i = 0; i < user.getInventory().size(); i++) {
                    ItemStack stack = user.getInventory().getStack(i);
                    if (stack.isOf(ModItems.ARCANE_EYES) && stack.getCount() > 0) {
                        stack.decrement(1); // remove exactly one
                        removed = true;
                        break;
                    }
                }

                if (removed) {
                    eyes.setState(EyesComponent.EyeState.ARCANE);
                    MyComponents.EYES.sync(user);
                    user.sendMessage(Text.literal("You attach a pair of Arcane eyes to your Eye sockets!"), false);
                } else {
                    user.sendMessage(Text.literal("You don’t have any Arcane Eyes to reattach!"), false);
                }
            } else {
                user.sendMessage(Text.literal("Can't reattach eyes! You already have " + eyes.getState() + " eyes!"), false);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
