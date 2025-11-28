package com.unfleshed.renderer;

import com.unfleshed.block.SanguisVelumBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.joml.Quaternionf;

public class SanguisVelumBlockEntityRenderer implements BlockEntityRenderer<SanguisVelumBlockEntity> {

    public SanguisVelumBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(SanguisVelumBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        ItemStack stack = blockEntity.getStoredItem();
        if (stack == null) stack = new ItemStack(Items.AIR);

        World world = blockEntity.getWorld();
        matrices.push();
        matrices.translate(0.5, 1.2, 0.5);

        float time = world != null ? world.getTime() + tickDelta : tickDelta;
        float angleRadians = (float) Math.toRadians(time * 4f);
        matrices.multiply(new Quaternionf().rotateY(angleRadians));

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GROUND,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                world,
                0
        );

        matrices.pop();
    }
}
