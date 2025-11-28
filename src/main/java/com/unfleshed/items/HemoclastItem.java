package com.unfleshed.items;

import com.unfleshed.damage.ModDamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HemoclastItem extends SwordItem {

    public HemoclastItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {

            // Cooldown check
            if (user.getItemCooldownManager().isCoolingDown(this)) {
                return TypedActionResult.fail(user.getStackInHand(hand));
            }

            // Damage the player
            user.damage(ModDamageTypes.dismemberment(user.getWorld()), 5.0F);
            com.unfleshed.events.BloodlettingEvent.EVENT.invoker().onBloodletting(user);
            // Apply cooldown: 5 seconds = 100 ticks
            user.getItemCooldownManager().set(this, 100);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
