package com.combatsasality.scol.blocks.generic;

import com.combatsasality.scol.tiles.SoulGlassTile;
import com.combatsasality.scol.tiles.generic.BaseItemStackTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItemStackBlock extends BaseEntityBlock {
    public BaseItemStackBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BaseItemStackTile tile) {
            ItemStack heldItem = player.getMainHandItem();
            ItemStack item = tile.getItem();
            if (item.isEmpty()) {
                ItemStack copy = heldItem.copy();
                heldItem.shrink(1);
                copy.setCount(1);
                tile.setItem(copy);
            } else {
                if (heldItem.isEmpty()) {
                    player.setItemInHand(interactionHand, item);
                    tile.setItem(ItemStack.EMPTY);
                } else {
                    ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), item);
                    itemEntity.setNoPickUpDelay();
                    level.addFreshEntity(itemEntity);
                    tile.setItem(ItemStack.EMPTY);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseItemStackTile tile) {
                ItemStack stack = tile.getItem();
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
