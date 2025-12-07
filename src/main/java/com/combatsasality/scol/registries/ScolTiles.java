package com.combatsasality.scol.registries;

import com.combatsasality.scol.tiles.AltarTile;
import com.combatsasality.scol.tiles.PedestalTile;
import com.combatsasality.scol.tiles.SoulGlassTile;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolTiles extends AbstractRegistry<BlockEntityType<?>> {
    @ObjectHolder(value = MODID + ":soul_glass_tile", registryName = "block_entity_type")
    public static final BlockEntityType<SoulGlassTile> SOUL_GLASS = null;
    @ObjectHolder(value = MODID + ":altar_tile", registryName = "block_entity_type")
    public static final BlockEntityType<AltarTile> ALTAR = null;
    @ObjectHolder(value = MODID + ":pedestal_tile", registryName = "block_entity_type")
    public static final BlockEntityType<PedestalTile> PEDESTAL = null;

    public ScolTiles() {
        super(ForgeRegistries.BLOCK_ENTITY_TYPES);
        register("soul_glass_tile", () -> BlockEntityType.Builder.of(SoulGlassTile::new, ScolBlocks.SOUL_GLASS)
                .build(Util.fetchChoiceType(References.BLOCK_ENTITY, MODID + ":soul_glass_tile")));
        register("altar_tile", () -> BlockEntityType.Builder.of(AltarTile::new, ScolBlocks.ALTAR)
                .build(Util.fetchChoiceType(References.BLOCK_ENTITY, MODID + ":altar_tile")));
        register("pedestal_tile", () -> BlockEntityType.Builder.of(PedestalTile::new, ScolBlocks.PEDESTAL)
                .build(Util.fetchChoiceType(References.BLOCK_ENTITY, MODID + ":pedestal_tile")));
    }


}
