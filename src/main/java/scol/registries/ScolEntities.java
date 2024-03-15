package scol.registries;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import scol.Main;
import scol.entity.CustomItemEntity;
import scol.entity.IchigoVizard;
import scol.entity.Onryo;
import scol.entity.projectile.PowerWaveEntity;

public class ScolEntities extends AbstractRegistry<EntityType<?>> {
    @ObjectHolder(MODID + ":custom_item_entity_ent")
    public static EntityType<CustomItemEntity> CUSTOM_ITEM_ENTITY_ENT;
    @ObjectHolder(MODID + ":ichigo_vizard")
    public static EntityType<IchigoVizard> ICHIGO_VIZARD;
    @ObjectHolder(MODID + ":onryo")
    public static EntityType<Onryo> ONRYO;
    @ObjectHolder(MODID + ":power_wave")
    public static EntityType<PowerWaveEntity> POWER_WAVE;

    public ScolEntities() {
        super(ForgeRegistries.ENTITIES);
        register("custom_item_entity_ent", () -> EntityType.Builder.<CustomItemEntity>of(CustomItemEntity::new, EntityClassification.MISC).sized(0.25F, 2.0F)
                .setTrackingRange(64).setCustomClientFactory((spawnEntity, world) -> new CustomItemEntity(CUSTOM_ITEM_ENTITY_ENT, world))
                .setUpdateInterval(2).setShouldReceiveVelocityUpdates(true).build(MODID + ":custom_item_entity_ent"));
        register("ichigo_vizard", () -> EntityType.Builder.<IchigoVizard>of(IchigoVizard::new, EntityClassification.MISC).sized(0.75f, 1.85f)
                .setTrackingRange(64).setCustomClientFactory((spawnEntity, world) -> new IchigoVizard(ICHIGO_VIZARD, world))
                .build(Main.MODID + ":ichigo_vizard"));
        register("onryo", () -> EntityType.Builder.<Onryo>of(Onryo::new, EntityClassification.MISC
                ).sized(0.6f, 1.95f)
                .setTrackingRange(64).setCustomClientFactory((spawnEntity, world) -> new Onryo(ONRYO, world))
                .build(Main.MODID + ":onryo"));
        register("power_wave", () -> EntityType.Builder.<PowerWaveEntity>of(PowerWaveEntity::new, EntityClassification.MISC).sized(4.0F, 0.1F)
                .noSave()
                .setCustomClientFactory((spawnEntity, world) -> new PowerWaveEntity(ScolEntities.POWER_WAVE, world))
                .setTrackingRange(64).build(Main.MODID + ":power_wave"));
    }

}
