package com.unfleshed.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class OculonLaserRenderer {

    private static final List<LaserBeam> ACTIVE_BEAMS = new ArrayList<>();
    private static final float LASER_WIDTH = 0.2f;
    private static final int MAX_LASER_TICKS = 8;
    private static final Identifier LASER_TEXTURE = new Identifier("unfleshed", "textures/misc/red.png");

    // EnergySwirl allows standalone textures
    private static final RenderLayer LASER_LAYER = RenderLayer.getEnergySwirl(LASER_TEXTURE, 0f, 0f);

    public static void addLaserBeam(Vec3d start, Vec3d end) {
        ACTIVE_BEAMS.add(new LaserBeam(start, end));
    }

    public static void tick() {
        ACTIVE_BEAMS.removeIf(beam -> beam.age++ >= MAX_LASER_TICKS);
    }

    public static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta) {
        if (ACTIVE_BEAMS.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        int light = LightmapTextureManager.pack(15, 15);

        VertexConsumer buffer = vertexConsumers.getBuffer(LASER_LAYER);

        for (LaserBeam beam : ACTIVE_BEAMS) {
            renderLaserBeam(buffer, matrices.peek(), cameraPos, beam.start, beam.end, light);
        }
    }

    private static void renderLaserBeam(VertexConsumer buffer, MatrixStack.Entry matrix, Vec3d cameraPos,
                                        Vec3d start, Vec3d end, int light) {

        Vec3d startPos = start.subtract(cameraPos);
        Vec3d endPos = end.subtract(cameraPos);
        Vec3d dir = endPos.subtract(startPos).normalize();

        // Perpendicular vector for + quad
        Vec3d up = Math.abs(dir.y) > 0.99 ? new Vec3d(1, 0, 0) : new Vec3d(0, 1, 0);
        Vec3d right = dir.crossProduct(up).normalize().multiply(LASER_WIDTH);

        // First + quad
        drawDoubleSidedQuad(buffer, matrix,
                startPos.subtract(right), startPos.add(right),
                endPos.add(right), endPos.subtract(right), light);

        // Second + quad (cross)
        Vec3d cross = right.crossProduct(dir).normalize().multiply(LASER_WIDTH);
        drawDoubleSidedQuad(buffer, matrix,
                startPos.add(cross), startPos.subtract(cross),
                endPos.subtract(cross), endPos.add(cross), light);

        // Rotated X quad (45°)
        Vec3d rotated1 = right.add(cross).normalize().multiply(LASER_WIDTH);
        Vec3d rotated2 = right.subtract(cross).normalize().multiply(LASER_WIDTH);
        drawDoubleSidedQuad(buffer, matrix,
                startPos.add(rotated1), startPos.subtract(rotated1),
                endPos.subtract(rotated1), endPos.add(rotated1), light);

        drawDoubleSidedQuad(buffer, matrix,
                startPos.add(rotated2), startPos.subtract(rotated2),
                endPos.subtract(rotated2), endPos.add(rotated2), light);
    }

    private static void drawDoubleSidedQuad(VertexConsumer buffer, MatrixStack.Entry matrix,
                                            Vec3d v0, Vec3d v1, Vec3d v2, Vec3d v3, int light) {
        Vec3d edge1 = v1.subtract(v0);
        Vec3d edge2 = v2.subtract(v0);
        Vec3d normal = edge1.crossProduct(edge2).normalize();

        float nx = (float) normal.x;
        float ny = (float) normal.y;
        float nz = (float) normal.z;

        // Front face
        drawTriangle(buffer, matrix, v0, v1, v2, nx, ny, nz, light);
        drawTriangle(buffer, matrix, v2, v3, v0, nx, ny, nz, light);

        // Back face
        drawTriangle(buffer, matrix, v2, v1, v0, -nx, -ny, -nz, light);
        drawTriangle(buffer, matrix, v0, v3, v2, -nx, -ny, -nz, light);
    }

    private static void drawTriangle(VertexConsumer buffer, MatrixStack.Entry matrix,
                                     Vec3d a, Vec3d b, Vec3d c,
                                     float nx, float ny, float nz, int light) {
        buffer.vertex(matrix.getPositionMatrix(), (float)a.x, (float)a.y, (float)a.z)
                .color(1f, 0f, 0f, 1f).texture(0f, 0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        buffer.vertex(matrix.getPositionMatrix(), (float)b.x, (float)b.y, (float)b.z)
                .color(1f, 0f, 0f, 1f).texture(1f, 0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        buffer.vertex(matrix.getPositionMatrix(), (float)c.x, (float)c.y, (float)c.z)
                .color(1f, 0f, 0f, 1f).texture(1f, 1f)
                .overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
    }

    private static class LaserBeam {
        public final Vec3d start;
        public final Vec3d end;
        public int age;

        public LaserBeam(Vec3d start, Vec3d end) {
            this.start = start;
            this.end = end;
            this.age = 0;
        }
    }
}
