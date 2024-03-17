package com.combatsasality.scol.registries;

import com.combatsasality.scol.blocks.SoulBlock;
import com.combatsasality.scol.blocks.SoulGlass;
import net.minecraft.block.*;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

public class ScolBlocks extends AbstractRegistry<Block> {
    // TODO: Replace default glass sound
    @ObjectHolder(MODID + ":soul_glass")
    public static SoulGlass SOUL_GLASS;
    @ObjectHolder(MODID + ":soul_block")
    public static SoulBlock SOUL_BLOCK;
    @ObjectHolder(MODID + ":aggressive_soul_block")
    public static SoulBlock AGGRESSIVE_SOUL_BLOCK;

    public ScolBlocks() {
        super(ForgeRegistries.BLOCKS);
        register("soul_glass", SoulGlass::new);
        register("soul_block", SoulBlock::new);
        register("aggressive_soul_block", SoulBlock::new);
    }

    @Override
    protected void register(String name, Supplier<Block> supplier) {
        super.register(name, supplier);
    }

}
