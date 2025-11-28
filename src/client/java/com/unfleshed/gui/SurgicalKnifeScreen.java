package com.unfleshed.gui;

import com.unfleshed.Components.MyComponents;
import com.unfleshed.network.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SurgicalKnifeScreen extends Screen {

    public SurgicalKnifeScreen() {
        super(Text.literal("Select Body Part"));
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int cy = height / 2;

        // Eyes button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Eyes"), b -> {
            PacketByteBuf buf = PacketByteBufs.create();
            ClientPlayNetworking.send(ModPackets.SURGICAL_KNIFE_EYES, buf);
            close();

        }).dimensions(cx - 50, cy - 40, 100, 20).build());

        // Tongue button (still placeholder)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Tongue"), b -> {
            System.out.println("Tongue");
            close();
        }).dimensions(cx - 50, cy - 10, 100, 20).build());

        // Fingers button (still placeholder)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Fingers"), b -> {
            System.out.println("Fingers");
            close();

        }).dimensions(cx - 50, cy + 20, 100, 20).build());

        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> {
            close();
        }).dimensions(cx - 50, cy + 50, 100, 20).build());
    }
}
