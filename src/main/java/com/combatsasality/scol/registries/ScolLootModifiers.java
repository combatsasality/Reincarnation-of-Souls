package com.combatsasality.scol.registries;

import com.combatsasality.scol.handlers.lootModifiers.EntityAdditionModifier;
import com.combatsasality.scol.handlers.lootModifiers.StructureAdditionModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class ScolLootModifiers extends AbstractRegistry<GlobalLootModifierSerializer<?>> {
    public ScolLootModifiers() {
        super(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS);
        // Structure Addition Modifiers
        register("handle_in_mineshaft", () -> new StructureAdditionModifier.Serializer(0.65F));
        register("handle_in_igloo", () -> new StructureAdditionModifier.Serializer(0.40F));
        register("handle_in_pillager_outpost", () -> new StructureAdditionModifier.Serializer(0.65F));
        register("inactive_ring_in_netherbridge", () -> new StructureAdditionModifier.Serializer(0.70F));
        register("part_mask_in_end_city", () -> new StructureAdditionModifier.Serializer(0.75F));

        // Entity Addition Modifiers
        register("part_mask_second_from_wither", () -> new EntityAdditionModifier.Serializer(0.98F, 0.01F));
        register("sound_disk_from_pig", () -> new EntityAdditionModifier.Serializer(0.99F, 0F));
    }
}
