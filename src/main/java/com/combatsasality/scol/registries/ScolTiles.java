package com.combatsasality.scol.registries;

import com.combatsasality.scol.items.Soul;
import com.combatsasality.scol.tileentity.SoulGlassTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolTiles extends AbstractRegistry<TileEntityType<?>> {
    @ObjectHolder(MODID + ":soul_glass_tile")
    public static TileEntityType<SoulGlassTile> SOUL_GLASS;
    public ScolTiles() {
        super(ForgeRegistries.TILE_ENTITIES);
        register("soul_glass_tile", () -> TileEntityType.Builder.of(SoulGlassTile::new, ScolBlocks.SOUL_GLASS).build(null));
    }

}
