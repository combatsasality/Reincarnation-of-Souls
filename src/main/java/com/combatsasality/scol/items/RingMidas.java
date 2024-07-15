package com.combatsasality.scol.items;

import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RingMidas extends Item implements ICurioItem, ITab {
    public RingMidas() {
        super(new Properties().stacksTo(1).rarity(Rarity.create("GOLD", ChatFormatting.GOLD)));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }
}
