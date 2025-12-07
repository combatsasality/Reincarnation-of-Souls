package com.combatsasality.scol.tiles;

import com.combatsasality.scol.registries.ScolTiles;
import com.combatsasality.scol.tiles.generic.BaseTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SoulGlassTile extends BaseTile {
    public String owner;


    public SoulGlassTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SoulGlassTile(BlockPos pos, BlockState state) {
        this(ScolTiles.SOUL_GLASS, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("owner", this.owner);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.owner = tag.getString("owner");
    }
}
