package com.combatsasality.scol.blocks;

import com.combatsasality.scol.registries.ScolTiles;
import com.combatsasality.scol.tileentity.SoulGlassTile;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class SoulGlass extends AbstractGlassBlock {

    public SoulGlass() {
        super(AbstractBlock.Properties.copy(Blocks.GLASS).strength(-1.0F, 3600000.0F).noDrops());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ScolTiles.SOUL_GLASS.create();
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity player, ItemStack stack) {
        if (world.isClientSide) return;
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof SoulGlassTile && player instanceof PlayerEntity) {
            SoulGlassTile soulGlassTile = (SoulGlassTile) tile;
            soulGlassTile.owner = player.getUUID();
        }
        super.setPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof SoulGlassTile) {
            SoulGlassTile soulGlassTile = (SoulGlassTile) tile;
            if (player.getUUID().equals(soulGlassTile.owner)) {
                world.destroyBlock(pos, true);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this)));
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
}
