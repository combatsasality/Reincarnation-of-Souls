package com.combatsasality.scol.registries;

import com.combatsasality.scol.blocks.Altar;
import com.combatsasality.scol.blocks.Pedestal;
import com.combatsasality.scol.blocks.SoulBlock;
import com.combatsasality.scol.blocks.SoulGlass;
import net.minecraft.block.*;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ScolBlocks extends AbstractRegistry<Block> {
    private static final Map<String, Block> BLOCKS = new HashMap<>();
    // TODO: Replace default glass sound
    @ObjectHolder(MODID + ":soul_glass")
    public static SoulGlass SOUL_GLASS;
    @ObjectHolder(MODID + ":soul_block")
    public static SoulBlock SOUL_BLOCK;
    @ObjectHolder(MODID + ":aggressive_soul_block")
    public static SoulBlock AGGRESSIVE_SOUL_BLOCK;
    @ObjectHolder(MODID + ":pedestal")
    public static Pedestal PEDESTAL;
    @ObjectHolder(MODID + ":altar")
    public static Altar ALTAR;

    public ScolBlocks() {
        super(ForgeRegistries.BLOCKS);
        register("soul_glass", SoulGlass::new);
        register("soul_block", SoulBlock::new);
        register("aggressive_soul_block", SoulBlock::new);
        register("pedestal", Pedestal::new);
        register("altar", Altar::new);
    }

    @Override
    protected void register(String name, Supplier<Block> supplier) {
        super.register(name, supplier);
        BLOCKS.put(name, supplier.get());
    }

    public static Map<String, Block> getBlocks() {
        return Collections.unmodifiableMap(BLOCKS);
    }

}
