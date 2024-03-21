package com.combatsasality.scol.tileentity;

import com.combatsasality.scol.registries.ScolTiles;
import com.combatsasality.scol.tileentity.generic.BaseTile;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class SoulGlassTile extends BaseTile {

    public String owner;
    public SoulGlassTile() {
        super(ScolTiles.SOUL_GLASS);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putString("owner", this.owner);
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.owner = nbt.getString("owner");
    }

}
