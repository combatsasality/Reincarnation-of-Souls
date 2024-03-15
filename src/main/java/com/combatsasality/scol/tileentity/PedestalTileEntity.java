package com.combatsasality.scol.tileentity;

import com.combatsasality.scol.Main;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class PedestalTileEntity extends TileEntity {

    @ObjectHolder(Main.MODID + ":pedestal")
    public static TileEntityType<PedestalTileEntity> TYPE;

    public PedestalTileEntity(TileEntityType<? extends TileEntity> type) {
        super(type);
    }
}
