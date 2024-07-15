package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.tiles.PedestalTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PedestalTileRenderer implements BlockEntityRenderer<PedestalTile> {

    public PedestalTileRenderer(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(PedestalTile tile, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        var minecraft = Minecraft.getInstance();
        ItemStack stack = tile.getItem();
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.35D, 0.5D);
            float scale = stack.getItem() instanceof BlockItem ? 0.95F : 0.75F;
            poseStack.scale(scale, scale, scale);
            double tick = System.currentTimeMillis() / 800.0D;
            poseStack.translate(0.0D, Math.sin(tick % (2 * Math.PI)) * 0.065D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
            minecraft.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, i, i1, poseStack, multiBufferSource, minecraft.level, 0);
            poseStack.popPose();
        }
    }
}
