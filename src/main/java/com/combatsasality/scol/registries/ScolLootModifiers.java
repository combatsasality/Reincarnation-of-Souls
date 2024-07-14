package com.combatsasality.scol.registries;

import com.combatsasality.scol.handlers.loot.EntityAdditionModifier;
import com.combatsasality.scol.handlers.loot.StructureAdditionModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class ScolLootModifiers extends AbstractRegistry<Codec<? extends IGlobalLootModifier>>{

    public ScolLootModifiers() {
        super(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
        register("structure_addition", StructureAdditionModifier.CODEC::get);
        register("entity_addition", EntityAdditionModifier.CODEC::get);
    }
}
