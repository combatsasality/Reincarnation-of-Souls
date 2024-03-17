package com.combatsasality.scol.tileentity;

import com.combatsasality.scol.registries.ScolTiles;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class SoulGlassTile extends TileEntity {

    public UUID owner;
    public SoulGlassTile() {
        super(ScolTiles.SOUL_GLASS);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putUUID("owner", owner);
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.hasUUID("owner")) owner = nbt.getUUID("owner");
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUUID("owner", owner);
        return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        owner = nbt.getUUID("owner");
    }
}
