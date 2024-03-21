package com.combatsasality.scol.tileentity.generic;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public abstract class BaseItemStackTile extends BaseTile {
    protected ItemStack item = ItemStack.EMPTY;
    public BaseItemStackTile(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if (!item.isEmpty()) {
            nbt.put("item", item.save(new CompoundNBT()));
        }
        return super.save(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        item = ItemStack.of(nbt.getCompound("item"));
        super.load(state, nbt);
    }
    public ItemStack getItem() {
        return item;
    }
    public void setItem(ItemStack item) {
        this.item = item;
        setChanged();
    }

}
