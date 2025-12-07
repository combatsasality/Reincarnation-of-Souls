package com.combatsasality.scol.registries;

import com.combatsasality.scol.entity.CustomItemEntity;
import com.combatsasality.scol.entity.IchigoVizard;
import com.combatsasality.scol.entity.projectile.PowerWaveEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolEntities extends AbstractRegistry<EntityType<?>> {
    @ObjectHolder(value = MODID + ":ichigo_vizard", registryName = "entity_type")
    public static final EntityType<IchigoVizard> ICHIGO_VAZARD = null;
    @ObjectHolder(value = MODID + ":custom_item_entity", registryName = "entity_type")
    public static final EntityType<CustomItemEntity> CUSTOM_ITEM_ENTITY = null;
    @ObjectHolder(value = MODID + ":power_wave", registryName = "entity_type")
    public static final EntityType<PowerWaveEntity> POWER_WAVE_ENTITY = null;


    public ScolEntities() {
        super(ForgeRegistries.ENTITY_TYPES);
        this.register("ichigo_vizard", () -> EntityType.Builder.<IchigoVizard>of(
                IchigoVizard::new, MobCategory.MONSTER).sized(0.75f, 1.85f).setTrackingRange(64)
                .setCustomClientFactory((spawnEntity, level) ->
                new IchigoVizard(ICHIGO_VAZARD, level))
                .build(MODID + ":ichigo_vizard"));

        this.register("custom_item_entity", () -> EntityType.Builder.<CustomItemEntity>of(
                CustomItemEntity::new, MobCategory.MISC).sized(0.25F, 2.0F).setTrackingRange(64)
                .setCustomClientFactory(((spawnEntity, level) -> new CustomItemEntity(CUSTOM_ITEM_ENTITY, level)))
                .setUpdateInterval(2).setShouldReceiveVelocityUpdates(true)
                .build(MODID + ":custom_item_entity"));


        this.register("power_wave", () -> EntityType.Builder.<PowerWaveEntity>of(
                PowerWaveEntity::new, MobCategory.MISC).sized(4.0F, 0.1F)
                .noSave()
                .setCustomClientFactory(((spawnEntity, level) -> new PowerWaveEntity(POWER_WAVE_ENTITY, level)))
                .setTrackingRange(64)
                .build(MODID + ":power_wave"));
    }
}
