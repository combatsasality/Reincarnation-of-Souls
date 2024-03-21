package com.combatsasality.scol.registries;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.items.*;
import com.combatsasality.scol.items.generic.ISoulMaterial;
import com.combatsasality.scol.items.generic.ItemBase;
import net.minecraft.item.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolItems extends AbstractRegistry<Item> {
    @ObjectHolder(MODID + ":test_item")
    public static TestItem TEST_ITEM;
    @ObjectHolder(MODID + ":frostmourne")
    public static FrostMourne FROSTMOURNE;
    @ObjectHolder(MODID + ":frostmourne_handle")
    public static ItemBase HANDLE_FROST;
    @ObjectHolder(MODID + ":frostmourne_blade")
    public static ItemBase BLADE_FROST;
    @ObjectHolder(MODID + ":phoenix_ring")
    public static PhoenixRing PHOENIX_RING;
    @ObjectHolder(MODID + ":phoenix_ring_inactive")
    public static InactivePhoenixRing INACTIVE_PHOENIX_RING;
    @ObjectHolder(MODID + ":dragon_soul")
    public static ItemBase DRAGON_SOUL;
    @ObjectHolder(MODID + ":wither_soul")
    public static ItemBase WITHER_SOUL;
    @ObjectHolder(MODID + ":world_wing")
    public static WorldWing WORLD_WING;
    @ObjectHolder(MODID + ":part_mask_first")
    public static ItemBase PART_MASK_FIRST;
    @ObjectHolder(MODID + ":part_mask_second")
    public static ItemBase PART_MASK_SECOND;
    @ObjectHolder(MODID + ":part_mask_third")
    public static ItemBase PART_MASK_THIRD;
    @ObjectHolder(MODID + ":part_mask_fourth")
    public static ItemBase PART_MASK_FOURTH;
    @ObjectHolder(MODID + ":summon_mask")
    public static SummonMask SUMMON_MASK;
    @ObjectHolder(MODID + ":zangetsu")
    public static Zangetsu ZANGETSU;
    @ObjectHolder(MODID + ":ring_midas")
    public static RingMidas RING_MIDAS;
    @ObjectHolder(MODID + ":music_disc_metal_3")
    public static MusicDiscItem MUSIC_DISC_METAL_3;
    @ObjectHolder(MODID + ":music_disc_silent_relapse")
    public static MusicDiscItem MUSIC_DISC_SILENT_RELAPSE;
    @ObjectHolder(MODID + ":onryo_spawn_egg")
    public static ForgeSpawnEggItem ONRYO_SPAWN_EGG;
    @ObjectHolder(MODID + ":soul")
    public static Soul SOUL;
    @ObjectHolder(MODID + ":aggressive_soul")
    public static Soul AGGRESSIVE_SOUL;
    @ObjectHolder(MODID + ":friendly_soul")
    public static Soul FRIENDLY_SOUL;

    public ScolItems() {
        super(ForgeRegistries.ITEMS);
        register("test_item", TestItem::new);
        register("frostmourne", FrostMourne::new);
        register("frostmourne_handle", () -> new ItemBase(new Item.Properties().fireResistant()));
        register("frostmourne_blade", () -> new ItemBase(new Item.Properties().fireResistant()));
        register("phoenix_ring", PhoenixRing::new);
        register("phoenix_ring_inactive", InactivePhoenixRing::new);
        register("dragon_soul", () -> new ItemBase(new Item.Properties().fireResistant().rarity(Rarity.EPIC)));
        register("wither_soul", () -> new ItemBase(new Item.Properties().fireResistant().rarity(Rarity.create("RED", TextFormatting.RED))));
        register("world_wing", WorldWing::new);
        register("part_mask_first", ItemBase::new);
        register("part_mask_second", ItemBase::new);
        register("part_mask_third", ItemBase::new);
        register("part_mask_fourth", ItemBase::new);
        register("summon_mask", SummonMask::new);
        register("zangetsu", Zangetsu::new);
        register("ring_midas", RingMidas::new);
        Item.Properties discProperties = new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).tab(Main.TAB);
        register("music_disc_metal_3", () -> new MusicDiscItem(1, () -> ScolSounds.MUSIC_METAL_3, discProperties));
        register("music_disc_silent_relapse", () -> new MusicDiscItem(2, () -> ScolSounds.MUSIC_SILENT_RELAPSE, discProperties));
        register("onryo_spawn_egg", () -> new ForgeSpawnEggItem(() -> ScolEntities.ONRYO, 0xFFFFFF, 0x40E0D0, new Item.Properties().tab(ItemGroup.TAB_MISC)));
        register("soul", () -> new Soul( 9300, ISoulMaterial.SoulType.NEGATIVE));
        register("aggressive_soul", () -> new Soul(18600, ISoulMaterial.SoulType.NEGATIVE));
        register("friendly_soul", () -> new Soul(4500, ISoulMaterial.SoulType.FRIENDLY));



        //Blocks

//        // TODO: Rewrite this
        register("soul_glass", () -> new BlockItem(ScolBlocks.SOUL_GLASS, new Item.Properties().tab(Main.TAB)));
        register("soul_block", () -> new BlockItem(ScolBlocks.SOUL_BLOCK, new Item.Properties().tab(Main.TAB)));
        register("aggressive_soul_block", () -> new BlockItem(ScolBlocks.AGGRESSIVE_SOUL_BLOCK, new Item.Properties().tab(Main.TAB)));
        register("pedestal", () -> new BlockItem(ScolBlocks.PEDESTAL, new Item.Properties().tab(Main.TAB)));
        register("altar", () -> new BlockItem(ScolBlocks.ALTAR, new Item.Properties().tab(Main.TAB)));

    }


}
