package com.unfleshed.items;

import com.unfleshed.network.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.player.PlayerEntity;



public class SurgicalKnifeItem extends SwordItem {

    public SurgicalKnifeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ServerPlayNetworking.send((ServerPlayerEntity) user, ModPackets.OPEN_SURGICAL_KNIFE_GUI, PacketByteBufs.create());
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

}
