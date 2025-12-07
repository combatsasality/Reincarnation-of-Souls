package com.combatsasality.scol.blocks.generic;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickAbleTile extends EntityBlock {

    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? getClientTicker(level, state, type) : getServerTicker(level, state, type);
    }

    default <T extends BlockEntity> BlockEntityTicker<T> getClientTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    default <T extends BlockEntity> BlockEntityTicker<T> getServerTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @SuppressWarnings("unchecked")
    static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTicker(
            BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker) {
        return typeA == typeB ? (BlockEntityTicker<A>) ticker : null;
    }
}
