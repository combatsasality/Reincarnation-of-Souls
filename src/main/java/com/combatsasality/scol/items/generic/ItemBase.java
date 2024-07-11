package com.combatsasality.scol.items.generic;

import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ItemBase extends Item implements ITab{
    public ItemBase(String regItem, Properties properties) {
        super(properties);
    }

    public ItemBase(String regItem) {
        super(new Properties());
    }

    public ItemBase(Properties properties) {
        super(properties);
    }

    public ItemBase() {
        super(new Properties());
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }
}
