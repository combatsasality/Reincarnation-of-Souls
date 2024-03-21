package com.combatsasality.scol.blocks.generic;

import com.combatsasality.scol.tileentity.generic.BaseItemStackTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class BaseItemStackBlock extends Block {
    public BaseItemStackBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof BaseItemStackTile) {
            BaseItemStackTile pedestalTile = (BaseItemStackTile) tile;
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack stack = pedestalTile.getItem();
            if (stack.isEmpty()) {
                ItemStack copy = heldItem.copy();
                heldItem.shrink(1);
                copy.setCount(1);
                pedestalTile.setItem(copy);
            } else {
                if (heldItem.isEmpty()) {
                    player.setItemInHand(hand, stack);
                    pedestalTile.setItem(ItemStack.EMPTY);
                } else {
                    ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack);
                    itemEntity.setNoPickUpDelay();
                    world.addFreshEntity(itemEntity);
                    pedestalTile.setItem(ItemStack.EMPTY);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof BaseItemStackTile) {
                BaseItemStackTile pedestalTile = (BaseItemStackTile) tileEntity;
                ItemStack stack = pedestalTile.getItem();
                if (!stack.isEmpty()) {
                    InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

}
