package com.unfleshed.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SurgicalKnifeScreen extends Screen {

    public SurgicalKnifeScreen() {
        super(Text.literal("Select Body Part"));
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int cy = height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Eyes"), b -> System.out.println("Eyes")).dimensions(cx - 50, cy - 40, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Tongue"), b -> System.out.println("Tongue")).dimensions(cx - 50, cy - 10, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Fingers"), b -> System.out.println("Fingers")).dimensions(cx - 50, cy + 20, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> close()).dimensions(cx - 50, cy + 50, 100, 20).build());
    }

}
