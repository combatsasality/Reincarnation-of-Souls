package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.tileentity.PedestalTile;
import com.combatsasality.scol.tileentity.generic.BaseItemStackTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class PedestalTileRenderer extends TileEntityRenderer<PedestalTile> {
    public PedestalTileRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PedestalTile tile, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        ItemStack stack = tile.getItem();
        if (!stack.isEmpty()) {
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 1.0D, 0.5D);
            float scale = stack.getItem() instanceof BlockItem ? 0.95F : 0.75F;
            matrixStack.scale(scale, scale, scale);
            double tick = System.currentTimeMillis() / 800.0D;
            matrixStack.translate(0.0D, Math.sin(tick % (2 * Math.PI)) * 0.065D, 0.0D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) ((tick * 40.0D) % 360)));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, i, i1, matrixStack, iRenderTypeBuffer);
            matrixStack.popPose();
        }
    }
}
