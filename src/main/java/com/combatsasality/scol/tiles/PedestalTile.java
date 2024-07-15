package com.combatsasality.scol.tiles;

import com.combatsasality.scol.registries.ScolTiles;
import com.combatsasality.scol.tiles.generic.BaseItemStackTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PedestalTile extends BaseItemStackTile {
    public PedestalTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public PedestalTile(BlockPos pos, BlockState state) {
        this(ScolTiles.PEDESTAL, pos, state);
    }
}
