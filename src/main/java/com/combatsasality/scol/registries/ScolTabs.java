package com.combatsasality.scol.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ScolTabs extends AbstractRegistry<CreativeModeTab> {

    public static final CreativeModeTab MAIN;

    static {
        MAIN = CreativeModeTab.builder().title(Component.translatable("itemGroup.scolTab")).icon(() -> new ItemStack(ScolItems.TEST_ITEM)).build();
    }

    public ScolTabs() {
        super(Registries.CREATIVE_MODE_TAB);
        this.register("tab_scol", () -> MAIN);
    }
}
