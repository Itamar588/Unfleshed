package com.unfleshed.network;

import com.unfleshed.Components.EyesComponent;
import com.unfleshed.Components.MyComponents;
import com.unfleshed.damage.ModDamageTypes;
import com.unfleshed.items.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

public class EyePacket {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.EYE_PACKET, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> handleEyesButton(player));
        });
    }
    private static void DismembermentEffects(PlayerEntity player) {
        player.damage(ModDamageTypes.dismemberment(player.getWorld()), 7.0F);

    }
    private static void handleEyesButton(ServerPlayerEntity player) {
        EyesComponent eyes = MyComponents.EYES.get(player);
        if (eyes == null) return; // safety check

        switch (eyes.getState()) {
            case HUMAN -> {
                // Give Human Eyes item and set state to BLIND
                player.getInventory().insertStack(new ItemStack(ModItems.HUMAN_EYES));
                eyes.setState(EyesComponent.EyeState.BLIND);
                MyComponents.EYES.sync(player);


            }
            case ARCANE -> {
                player.getInventory().insertStack(new ItemStack(ModItems.ARCANE_EYES));
                eyes.setState(EyesComponent.EyeState.BLIND);
                MyComponents.EYES.sync(player);
            }
            case BLIND -> player.sendMessage(Text.literal("You attempt to cut your eyes, but all you have are empty eye sockets."), false);
        }
    }
}
