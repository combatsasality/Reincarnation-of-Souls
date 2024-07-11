package com.combatsasality.scol.registries;

import com.combatsasality.scol.items.Frostmourne;
import com.combatsasality.scol.items.TestItem;
import com.combatsasality.scol.items.generic.ItemBase;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolItems extends AbstractRegistry<Item> {
    @ObjectHolder(value = MODID + ":test_item", registryName = "item")
    public static TestItem TEST_ITEM;
    @ObjectHolder(value = MODID + ":frostmourne", registryName = "item")
    public static Frostmourne FROSTMOURNE;

    public ScolItems() {
        super(ForgeRegistries.ITEMS);
        register("test_item", TestItem::new);
        register("frostmourne", Frostmourne::new);
    }
}
