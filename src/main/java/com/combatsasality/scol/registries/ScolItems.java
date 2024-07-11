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
    @ObjectHolder(value = MODID + ":part_mask_first", registryName = "item")
    public static ItemBase PART_MASK_FIRST;
    @ObjectHolder(value = MODID + ":part_mask_second", registryName = "item")
    public static ItemBase PART_MASK_SECOND;
    @ObjectHolder(value = MODID + ":part_mask_third", registryName = "item")
    public static ItemBase PART_MASK_THIRD;
    @ObjectHolder(value = MODID + ":part_mask_fourth", registryName = "item")
    public static ItemBase PART_MASK_FOURTH;


    public ScolItems() {
        super(ForgeRegistries.ITEMS);
        register("test_item", TestItem::new);
        register("frostmourne", Frostmourne::new);
        register("part_mask_first", ItemBase::new);
        register("part_mask_second", ItemBase::new);
        register("part_mask_third", ItemBase::new);
        register("part_mask_fourth", ItemBase::new);
    }
}
