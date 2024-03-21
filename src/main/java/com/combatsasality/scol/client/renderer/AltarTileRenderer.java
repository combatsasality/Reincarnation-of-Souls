package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.client.ScolRenderType;
import com.combatsasality.scol.registries.ScolBlocks;
import com.combatsasality.scol.tileentity.AltarTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;

public class AltarTileRenderer extends TileEntityRenderer<AltarTile> {
    public AltarTileRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(AltarTile altarTile, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        ItemStack stack = altarTile.getItem();
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
        BlockPos pos = altarTile.getBlockPos();
        World world = altarTile.getLevel();
        IVertexBuilder builder = iRenderTypeBuffer.getBuffer(ScolRenderType.GHOST);
        Minecraft minecraft = Minecraft.getInstance();
        matrixStack.pushPose();
        matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
        altarTile.getPedestalPositions().forEach(blockPos -> {
            if (world != null && world.isEmptyBlock(blockPos)) {
                matrixStack.pushPose();
                matrixStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                minecraft.getBlockRenderer().renderModel(ScolBlocks.PEDESTAL.defaultBlockState(), blockPos, world, matrixStack, builder, true, world.getRandom(), EmptyModelData.INSTANCE);
                matrixStack.popPose();
            }
        });
        matrixStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(AltarTile tile) {
        return true;
    }
}
