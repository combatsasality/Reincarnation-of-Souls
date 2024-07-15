package com.combatsasality.scol.registries;

import com.combatsasality.scol.blocks.Altar;
import com.combatsasality.scol.blocks.Pedestal;
import com.combatsasality.scol.blocks.SoulGlass;
import com.combatsasality.scol.items.generic.GenericBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ScolBlocks extends AbstractRegistry<Block> {
    private static final Map<ResourceLocation, BlockItemSupplier> BLOCK_ITEM_MAP = new HashMap<>();

    @ObjectHolder(value = MODID + ":soul_glass", registryName = "block")
    public static final SoulGlass SOUL_GLASS = null;
    @ObjectHolder(value = MODID + ":altar", registryName = "block")
    public static final Altar ALTAR = null;
    @ObjectHolder(value = MODID + ":pedestal", registryName = "block")
    public static final Pedestal PEDESTAL = null;

    public ScolBlocks() {
        super(ForgeRegistries.BLOCKS);
        this.register("soul_glass", SoulGlass::new, GenericBlockItem::new);
        this.register("altar", Altar::new, GenericBlockItem::new);
        this.register("pedestal", Pedestal::new, GenericBlockItem::new);
    }
    protected void register(String name, Supplier<Block> block, BlockItemSupplier item) {
        super.register(name, block);
        BLOCK_ITEM_MAP.put(new ResourceLocation(MODID, name), item);
    }

    protected static Map<ResourceLocation, BlockItemSupplier> getBlockItemMap() {
        return Collections.unmodifiableMap(BLOCK_ITEM_MAP);
    }


    @FunctionalInterface
    protected static interface BlockItemSupplier extends Function<Block, BlockItem> {
        @Override
        public BlockItem apply(Block block);
    }
}
