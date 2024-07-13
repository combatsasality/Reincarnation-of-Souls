package com.combatsasality.scol.registries;

import com.combatsasality.scol.items.Frostmourne;
import com.combatsasality.scol.items.InactivePhoenixRing;
import com.combatsasality.scol.items.PhoenixRing;
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
    @ObjectHolder(value = MODID + ":frostmourne_handle", registryName = "item")
    public static ItemBase HANDLE_FROST;
    @ObjectHolder(value = MODID + ":frostmourne_blade", registryName = "item")
    public static ItemBase BLADE_FROST;
    @ObjectHolder(value = MODID + ":phoenix_ring_inactive", registryName = "item")
    public static InactivePhoenixRing INACTIVE_PHOENIX_RING;
    @ObjectHolder(value = MODID + ":phoenix_ring", registryName = "item")
    public static PhoenixRing PHOENIX_RING;
    @ObjectHolder(value = MODID + ":dragon_soul", registryName = "item")
    public static ItemBase DRAGON_SOUL;
    @ObjectHolder(value = MODID + ":wither_soul", registryName = "item")
    public static ItemBase WITHER_SOUL;


    public ScolItems() {
        super(ForgeRegistries.ITEMS);
        register("test_item", TestItem::new);
        register("frostmourne", Frostmourne::new);
        register("part_mask_first", ItemBase::new);
        register("part_mask_second", ItemBase::new);
        register("part_mask_third", ItemBase::new);
        register("part_mask_fourth", ItemBase::new);
        register("frostmourne_handle", ItemBase::new);
        register("frostmourne_blade", ItemBase::new);
        register("phoenix_ring_inactive", InactivePhoenixRing::new);
        register("phoenix_ring", PhoenixRing::new);
        register("dragon_soul", ItemBase::new);
        register("wither_soul", ItemBase::new);
    }
}
