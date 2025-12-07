package com.combatsasality.scol.items.generic;

import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ItemBase extends Item implements ITab {
    public ItemBase(Properties properties) {
        super(properties);
    }

    public ItemBase() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }
}
