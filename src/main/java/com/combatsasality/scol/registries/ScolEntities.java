package com.combatsasality.scol.registries;

import com.combatsasality.scol.entity.IchigoVizard;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolEntities extends AbstractRegistry<EntityType<?>> {
    @ObjectHolder(value = MODID + ":ichigo_vizard", registryName = "entity_type")
    public static final EntityType<IchigoVizard> ICHIGO_VAZARD = null;


    public ScolEntities() {
        super(ForgeRegistries.ENTITY_TYPES);
        this.register("ichigo_vizard", () -> EntityType.Builder.<IchigoVizard>of(
                IchigoVizard::new, MobCategory.MONSTER).sized(0.75f, 1.85f).setTrackingRange(64)
                .setCustomClientFactory((spawnEntity, level) ->
                new IchigoVizard(ICHIGO_VAZARD, level))
                .build(MODID + ":ichigo_vizard"));
    }
}
