package com.unfleshed.gui;

import com.unfleshed.Components.EyesComponent;
import com.unfleshed.Components.MyComponents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Direction;

public class ArcaneHUD {
    private static final Identifier OVERLAY = new Identifier("unfleshed", "textures/gui/arcane_overlay.png");
    private static float rotation = 0f; // tracks rotation over time

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> renderOverlay(drawContext));
    }

    private static void renderOverlay(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Only show overlay if player has Arcane Eyes
        if (MyComponents.EYES.get(client.player).getState() != EyesComponent.EyeState.ARCANE) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Scale width to fit screen * 1.1, height = width
        int overlayWidth = (int) (screenWidth * 1.2);
        int overlayHeight = overlayWidth;

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // Update rotation slowly
        rotation += 0.05f;
        if (rotation >= 360f) rotation -= 360f;

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        // Move origin to center
        matrices.translate(centerX, centerY, 0);

        // Rotate around Z-axis
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

        // Move origin to top-left of overlay
        matrices.translate(-overlayWidth / 2f, -overlayHeight / 2f, 0);

        // Draw the overlay
        context.drawTexture(OVERLAY, 0, 0, 0, 0, overlayWidth, overlayHeight, overlayWidth, overlayHeight);

        matrices.pop();
    }
}
