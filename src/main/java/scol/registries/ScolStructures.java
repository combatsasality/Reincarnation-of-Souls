package scol.registries;

import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import scol.world.structures.Graveyard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScolStructures extends AbstractRegistry<Structure<?>>{

    private static final Map<String, StructureData> STRUCTURE_MAP = new HashMap<>();

    @ObjectHolder("scol:graveyard_combatsasality")
    public static Structure<NoFeatureConfig> GRAVEYARD_COMBATSASALITY = new Graveyard(NoFeatureConfig.CODEC, "graveyard_combatsasality");
    public static StructureFeature<?, ?> CONFIGURED_GRAVEYARD_COMBATSASALITY = GRAVEYARD_COMBATSASALITY.configured(IFeatureConfig.NONE);

    @ObjectHolder("scol:graveyard_desert")
    public static final Structure<NoFeatureConfig> GRAVEYARD_DESERT = new Graveyard(NoFeatureConfig.CODEC, "graveyard_desert");
    public static final StructureFeature<?, ?> CONFIGURED_GRAVEYARD_DESERT = GRAVEYARD_DESERT.configured(IFeatureConfig.NONE);

    @ObjectHolder("scol:graveyard_forest")
    public static final Structure<NoFeatureConfig> GRAVEYARD_FOREST = new Graveyard(NoFeatureConfig.CODEC, "graveyard_forest");
    public static final StructureFeature<?, ?> CONFIGURED_GRAVEYARD_FOREST = GRAVEYARD_FOREST.configured(IFeatureConfig.NONE);

    @ObjectHolder("scol:graveyard_mountains")
    public static final Structure<NoFeatureConfig> GRAVEYARD_MOUNTAINS = new Graveyard(NoFeatureConfig.CODEC, "graveyard_mountains");
    public static final StructureFeature<?, ?> CONFIGURED_GRAVEYARD_MOUNTAINS = GRAVEYARD_MOUNTAINS.configured(IFeatureConfig.NONE);

    @ObjectHolder("scol:graveyard_taiga")
    public static final Structure<NoFeatureConfig> GRAVEYARD_TAIGA = new Graveyard(NoFeatureConfig.CODEC, "graveyard_taiga");
    public static final StructureFeature<?, ?> CONFIGURED_GRAVEYARD_TAIGA = GRAVEYARD_TAIGA.configured(IFeatureConfig.NONE);

    @ObjectHolder("scol:graveyard_nether")
    public static final Structure<NoFeatureConfig> GRAVEYARD_NETHER = new Graveyard(NoFeatureConfig.CODEC, "graveyard_nether");
    public static final StructureFeature<?, ?> CONFIGURED_GRAVEYARD_NETHER = GRAVEYARD_NETHER.configured(IFeatureConfig.NONE);

    public ScolStructures() {
        super(ForgeRegistries.STRUCTURE_FEATURES);
        register("graveyard_combatsasality", GRAVEYARD_COMBATSASALITY, CONFIGURED_GRAVEYARD_COMBATSASALITY, 150, 100);
        register("graveyard_desert", GRAVEYARD_DESERT, CONFIGURED_GRAVEYARD_DESERT, 25, 20);
        register("graveyard_forest", GRAVEYARD_FOREST, CONFIGURED_GRAVEYARD_FOREST, 25, 20);
        register("graveyard_mountains", GRAVEYARD_MOUNTAINS, CONFIGURED_GRAVEYARD_MOUNTAINS, 25, 20);
        register("graveyard_taiga", GRAVEYARD_TAIGA, CONFIGURED_GRAVEYARD_TAIGA, 25, 20);
        register("graveyard_nether", GRAVEYARD_NETHER, CONFIGURED_GRAVEYARD_NETHER, 13, 10);
    }

    protected void register(String name, Structure<?> structure, StructureFeature<?, ?> configuredStructure, int average, int minAverage) {
        super.register(name, () -> structure);
        STRUCTURE_MAP.put(name, new StructureData(structure, configuredStructure, average, minAverage));
    }
    public static Map<String, StructureData> getStructureMap() {
        return Collections.unmodifiableMap(STRUCTURE_MAP);
    }

    public static class StructureData {
        public Structure<?> structure;
        public StructureFeature<?, ?> configuredStructure;
        public int average;
        public int minAverage;

        public StructureData(Structure<?> structure, StructureFeature<?, ?> configuredStructure, int average, int minAverage) {
            this.structure = structure;
            this.configuredStructure = configuredStructure;
            this.average = average;
            this.minAverage = minAverage;
        }
    }
}
