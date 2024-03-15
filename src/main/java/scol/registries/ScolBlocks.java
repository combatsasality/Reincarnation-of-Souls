package scol.registries;

import net.minecraft.block.*;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import scol.blocks.SoulBlock;

import java.util.function.Supplier;

public class ScolBlocks extends AbstractRegistry<Block> {
    // TODO: Replace default glass sound
    @ObjectHolder(MODID + ":soul_glass")
    public static AbstractGlassBlock SOUL_GLASS;
    @ObjectHolder(MODID + ":soul_block")
    public static SoulBlock SOUL_BLOCK;
    @ObjectHolder(MODID + ":aggressive_soul_block")
    public static SoulBlock AGGRESSIVE_SOUL_BLOCK;

    public ScolBlocks() {
        super(ForgeRegistries.BLOCKS);
        register("soul_glass", () -> new GlassBlock(AbstractBlock.Properties.copy(Blocks.GLASS)));
        register("soul_block", SoulBlock::new);
        register("aggressive_soul_block", SoulBlock::new);
    }

    @Override
    protected void register(String name, Supplier<Block> supplier) {
        super.register(name, supplier);
    }

}
