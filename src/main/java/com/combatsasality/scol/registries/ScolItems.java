package com.combatsasality.scol.registries;

import com.combatsasality.scol.items.*;
import com.combatsasality.scol.items.generic.ItemBase;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolItems extends AbstractRegistry<Item> {
    @ObjectHolder(value = MODID + ":test_item", registryName = "item")
    public static final TestItem TEST_ITEM = null;
    @ObjectHolder(value = MODID + ":frostmourne", registryName = "item")
    public static final Frostmourne FROSTMOURNE = null;
    @ObjectHolder(value = MODID + ":part_mask_first", registryName = "item")
    public static final ItemBase PART_MASK_FIRST = null;
    @ObjectHolder(value = MODID + ":part_mask_second", registryName = "item")
    public static final ItemBase PART_MASK_SECOND = null;
    @ObjectHolder(value = MODID + ":part_mask_third", registryName = "item")
    public static final ItemBase PART_MASK_THIRD = null;
    @ObjectHolder(value = MODID + ":part_mask_fourth", registryName = "item")
    public static final ItemBase PART_MASK_FOURTH = null;
    @ObjectHolder(value = MODID + ":frostmourne_handle", registryName = "item")
    public static final ItemBase HANDLE_FROST = null;
    @ObjectHolder(value = MODID + ":frostmourne_blade", registryName = "item")
    public static final ItemBase BLADE_FROST = null;
    @ObjectHolder(value = MODID + ":phoenix_ring_inactive", registryName = "item")
    public static final InactivePhoenixRing INACTIVE_PHOENIX_RING = null;
    @ObjectHolder(value = MODID + ":phoenix_ring", registryName = "item")
    public static final PhoenixRing PHOENIX_RING = null;
    @ObjectHolder(value = MODID + ":dragon_soul", registryName = "item")
    public static final ItemBase DRAGON_SOUL = null;
    @ObjectHolder(value = MODID + ":wither_soul", registryName = "item")
    public static final ItemBase WITHER_SOUL = null;
    @ObjectHolder(value = MODID + ":world_wing", registryName = "item")
    public static final WorldWing WORLD_WING = null;
    @ObjectHolder(value = MODID + ":music_disc_metal_3", registryName = "item")
    public static final RecordItem MUSIC_DISC_METAL_3 = null;
    @ObjectHolder(value = MODID + ":music_disc_silent_relapse", registryName = "item")
    public static final RecordItem MUSIC_DISC_SILENT_RELAPSE = null;


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
        register("world_wing", WorldWing::new);
        Item.Properties discProperties = new Item.Properties().rarity(Rarity.EPIC).stacksTo(1);
        register("music_disc_metal_3", () -> new RecordItem(1, () -> ScolSounds.MUSIC_METAL_3, discProperties, 2380));
        register("music_disc_silent_relapse", () -> new RecordItem(2, () -> ScolSounds.MUSIC_SILENT_RELAPSE, discProperties, 1420));

    }
}
