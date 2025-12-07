package com.combatsasality.scol.items.generic;

import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Author Aizistral-Studios\Enigmatic-Legacy
 */
public class GenericBlockItem extends BlockItem implements ITab {
    private final Supplier<CreativeModeTab> tab; // supplier cuz weird shit happens otherwise

    public GenericBlockItem(Block blockIn) {
        this(blockIn, GenericBlockItem.getDefaultProperties());
    }

    public GenericBlockItem(Block blockIn, Properties props) {
        this(blockIn, props, () -> ScolTabs.MAIN);
    }

    public GenericBlockItem(Block blockIn, Properties props, Supplier<CreativeModeTab> tab) {
        super(blockIn, props);
        this.tab = tab;
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return this.tab.get();
    }

    public static Properties getDefaultProperties() {
        Properties props = new Item.Properties();

        props.stacksTo(64);
        props.rarity(Rarity.COMMON);

        return props;
    }
}
