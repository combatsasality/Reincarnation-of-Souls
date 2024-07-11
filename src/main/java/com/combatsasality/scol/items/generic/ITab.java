package com.combatsasality.scol.items.generic;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public interface ITab {

    public CreativeModeTab getCreativeTab();

    public default List<ItemStack> getCreativeTabStacks() {
        if (this instanceof ItemLike item)
            return ImmutableList.of(new ItemStack(item));
        else
            return ImmutableList.of();
    }
}
