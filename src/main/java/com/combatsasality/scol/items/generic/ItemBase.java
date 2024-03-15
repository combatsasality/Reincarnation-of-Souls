package com.combatsasality.scol.items.generic;

import com.combatsasality.scol.Main;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    public ItemBase(String regItem, Properties properties) {
        super(properties.tab(Main.TAB));
        this.setRegistryName(regItem);
    }

    public ItemBase(String regItem) {
        super(new Properties().tab(Main.TAB));
        this.setRegistryName(regItem);
    }

    public ItemBase(Properties properties) {
        super(properties.tab(Main.TAB));
    }

    public ItemBase() {
        super(new Properties().tab(Main.TAB));
    }

}
