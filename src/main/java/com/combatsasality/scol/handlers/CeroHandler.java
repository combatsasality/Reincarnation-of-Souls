package com.combatsasality.scol.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CeroHandler {
    /**
     * @param glowAlpha  outer glow transparency (0 = invisible, 1 = opaque; vanilla uses 0.125)
     * @param spinSpeed  inner beam rotation speed multiplier (1 = vanilla, 0 = no rotation, negative = reverse)
     */
    public static void renderCero(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation beamTexture,
                                  float partialTick, float textureScale, long gameTime,
                                  float yOffset, float height, float[] colors, float[] outerColors,
                                  float beamRadius, float glowRadius,
                                  float glowAlpha, float spinSpeed) {
        float topY = yOffset + height;

        poseStack.pushPose();

        // animation time: 40-tick loop (used for texture scrolling)
        float time = (float) Math.floorMod(gameTime, 40) + partialTick;
        // texture scroll direction (up or down, depending on the sign of height)
        float scrollTime = height < 0 ? time : -time;
        // V offset in range 0..1, makes the beam look like it's flowing
        float textureScroll = Mth.frac(scrollTime * 0.2F - (float) Mth.floor(scrollTime * 0.1F));

        // continuous rotation angle (no jumps for any speed)
        float spinAngle = (float) (((gameTime + (double) partialTick) * 2.25D * spinSpeed) % 360.0D);

        float red = colors[0];
        float green = colors[1];
        float blue = colors[2];

        // ===== Inner beam: opaque, rotating =====
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(spinAngle - 45.0F));

        float beamVStart = -1.0F + textureScroll;
        float beamVEnd = height * textureScale * (0.5F / beamRadius) + beamVStart;

        VertexConsumer beamConsumer = bufferSource.getBuffer(RenderType.beaconBeam(beamTexture, false));

        renderPart(poseStack, beamConsumer,
                red, green, blue, 1.0F,       // color and alpha
                yOffset, topY,                // bottom and top of the beam
                0.0F, beamRadius,             // corner 0 (x, z)
                beamRadius, 0.0F,             // corner 1
                -beamRadius, 0.0F,            // corner 2
                0.0F, -beamRadius,            // corner 3
                0.0F, 1.0F,                   // U: min, max
                beamVEnd, beamVStart);        // V: min, max

        // bottom cap (corner order matters: counter-clockwise when viewed from below)
        renderCap(poseStack, beamConsumer,
                red, green, blue, 1.0F,
                yOffset,
                0.0F, beamRadius,             // corner 0
                -beamRadius, 0.0F,            // corner 2
                0.0F, -beamRadius,            // corner 3
                beamRadius, 0.0F,             // corner 1
                0.0F, 1.0F, 0.0F, 1.0F);

        poseStack.popPose();

        // ===== Outer glow: translucent, not rotating =====
        // start the glow a bit before the inner beam so it wraps around the beam's start
        float glowPadding = glowRadius - beamRadius;
        float glowBottom = yOffset - glowPadding;

        float glowVStart = -1.0F + textureScroll;
        float glowVEnd = height * textureScale + glowVStart;
        float outerRed = outerColors[0];
        float outerGreen = outerColors[1];
        float outerBlue = outerColors[2];

        VertexConsumer glowConsumer = bufferSource.getBuffer(RenderType.beaconBeam(beamTexture, true));

        // outward-facing walls (visible from outside)
        renderPart(poseStack, glowConsumer,
                outerRed, outerGreen, outerBlue, glowAlpha,
                yOffset, topY,
                -glowRadius, -glowRadius,     // corner 0
                -glowRadius, glowRadius,      // corner 1
                glowRadius, -glowRadius,      // corner 2
                glowRadius, glowRadius,       // corner 3
                0.0F, 1.0F,
                glowVEnd, glowVStart);

        // inward-facing walls (visible from inside / from the beam's ends)
        renderPart(poseStack, glowConsumer,
                outerRed, outerGreen, outerBlue, glowAlpha,
                yOffset, topY,
                -glowRadius, -glowRadius,     // corner 0
                glowRadius, -glowRadius,      // corner 1
                -glowRadius, glowRadius,      // corner 2
                glowRadius, glowRadius,       // corner 3
                0.0F, 1.0F,
                glowVEnd, glowVStart);

        poseStack.popPose();
    }

    /**
     * Renders a spinning cube: opaque inner cube + translucent outer glow.
     * The cube is centered at the current poseStack origin.
     *
     * @param time        animation time, e.g. entity.tickCount + partialTick
     * @param speed       rotation speed multiplier (1 = default, 0 = no rotation, negative = reverse)
     * @param colors      inner cube color (r, g, b in 0..1)
     * @param outerColors outer glow color (r, g, b in 0..1)
     * @param innerSize   inner cube size in blocks
     * @param glowSize    outer glow size in blocks (should be bigger than innerSize)
     * @param glowAlpha   outer glow transparency (0 = invisible, 1 = opaque)
     */
    public static void renderGlowCube(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture,
                                      float time, float speed,
                                      float[] colors, float[] outerColors,
                                      float innerSize, float glowSize, float glowAlpha) {
        float red = colors[0];
        float green = colors[1];
        float blue = colors[2];

        float outerRed = outerColors[0];
        float outerGreen = outerColors[1];
        float outerBlue = outerColors[2];

        // keep the angle in 0..360 to avoid float precision loss over time
        float angle = (time * speed) % 360.0F;

        poseStack.pushPose();

        // spin around all three axes at different speeds
        poseStack.mulPose(Axis.YP.rotationDegrees(angle * 3.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(angle * 2.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle * 1.5F));

        // ===== Inner cube: opaque =====
        renderCube(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(texture, false)),
                red, green, blue, 1.0F, innerSize / 2.0F);

        // ===== Outer glow: translucent, slightly bigger =====
        renderCube(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(texture, true)),
                outerRed, outerGreen, outerBlue, glowAlpha, glowSize / 2.0F);

        poseStack.popPose();
    }



    /**
     * Renders a cube centered at the origin with the given half size.
     * Every face is wound counter-clockwise when viewed from outside.
     */
    private static void renderCube(PoseStack poseStack, VertexConsumer consumer,
                                   float red, float green, float blue, float alpha, float h) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m = pose.pose();
        Matrix3f n = pose.normal();

        // front (+Z)
        cubeFace(m, n, consumer, red, green, blue, alpha,
                -h, -h,  h,   h, -h,  h,   h,  h,  h,  -h,  h,  h);
        // back (-Z)
        cubeFace(m, n, consumer, red, green, blue, alpha,
                h, -h, -h,  -h, -h, -h,  -h,  h, -h,   h,  h, -h);
        // right (+X)
        cubeFace(m, n, consumer, red, green, blue, alpha,
                h, -h,  h,   h, -h, -h,   h,  h, -h,   h,  h,  h);
        // left (-X)
        cubeFace(m, n, consumer, red, green, blue, alpha,
                -h, -h, -h,  -h, -h,  h,  -h,  h,  h,  -h,  h, -h);
        // top (+Y)
        cubeFace(m, n, consumer, red, green, blue, alpha,
                -h,  h,  h,   h,  h,  h,   h,  h, -h,  -h,  h, -h);
        // bottom (-Y)
        cubeFace(m, n, consumer, red, green, blue, alpha,
                -h, -h, -h,   h, -h, -h,   h, -h,  h,  -h, -h,  h);
    }

    /**
     * Renders one face from 4 corners: bottom-left, bottom-right, top-right, top-left
     * (as seen from outside the cube).
     */
    private static void cubeFace(Matrix4f m, Matrix3f n, VertexConsumer consumer,
                                 float red, float green, float blue, float alpha,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3) {
        addVertex(m, n, consumer, red, green, blue, alpha, y0, x0, z0, 0.0F, 1.0F);
        addVertex(m, n, consumer, red, green, blue, alpha, y1, x1, z1, 1.0F, 1.0F);
        addVertex(m, n, consumer, red, green, blue, alpha, y2, x2, z2, 1.0F, 0.0F);
        addVertex(m, n, consumer, red, green, blue, alpha, y3, x3, z3, 0.0F, 0.0F);
    }

    /**
     * Renders a horizontal quad at the given height.
     * Corners must be passed in counter-clockwise order as seen from the side that should be visible.
     */
    private static void renderCap(PoseStack poseStack, VertexConsumer consumer,
                                  float red, float green, float blue, float alpha,
                                  float y,
                                  float x0, float z0, float x1, float z1,
                                  float x2, float z2, float x3, float z3,
                                  float minU, float maxU, float minV, float maxV) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, y, x0, z0, minU, minV);
        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, y, x1, z1, minU, maxV);
        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, y, x2, z2, maxU, maxV);
        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, y, x3, z3, maxU, minV);
    }

    /**
     * Renders a vertical "column" made of 4 side faces.
     * The column is defined by 4 corners on the XZ plane; each face connects two of them.
     */
    private static void renderPart(PoseStack poseStack, VertexConsumer consumer,
                                   float red, float green, float blue, float alpha,
                                   float minY, float maxY,
                                   float x0, float z0, float x1, float z1,
                                   float x2, float z2, float x3, float z3,
                                   float minU, float maxU, float minV, float maxV) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();     // position transform (translate/rotate/scale)
        Matrix3f normalMatrix = pose.normal(); // normal transform (rotation only)

        // one face per side of the column
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x0, z0, x1, z1, minU, maxU, minV, maxV);
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x3, z3, x2, z2, minU, maxU, minV, maxV);
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x1, z1, x3, z3, minU, maxU, minV, maxV);
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x2, z2, x0, z0, minU, maxU, minV, maxV);
    }

    /**
     * Renders one vertical quad (4 vertices) between two points on the XZ plane,
     * stretched from minY to maxY.
     */
    private static void renderQuad(Matrix4f poseMatrix, Matrix3f normalMatrix, VertexConsumer consumer,
                                   float red, float green, float blue, float alpha,
                                   float minY, float maxY,
                                   float startX, float startZ, float endX, float endZ,
                                   float minU, float maxU, float minV, float maxV) {
        // vertex order defines which side of the face is visible
        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, maxY, startX, startZ, maxU, minV); // top of start edge
        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, startX, startZ, maxU, maxV); // bottom of start edge
        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, endX, endZ, minU, maxV);     // bottom of end edge
        addVertex(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, maxY, endX, endZ, minU, minV);     // top of end edge
    }

    /**
     * Adds a single vertex to the buffer.
     * Element order must match the vertex format: position -> color -> uv -> overlay -> light -> normal.
     */
    private static void addVertex(Matrix4f poseMatrix, Matrix3f normalMatrix, VertexConsumer consumer,
                                  float red, float green, float blue, float alpha,
                                  float y, float x, float z, float u, float v) {
        consumer.vertex(poseMatrix, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)                    // full brightness (block 15, sky 15), glows in the dark
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
