package scol.world.structures;

import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import scol.Main;

public class Graveyard extends Structure<NoFeatureConfig> {
    public String resourceLocationPool;
    public boolean isNetherGraveyard = false;

    public Graveyard(Codec<NoFeatureConfig> codec, String resourceLocationPool, boolean isNetherGraveyard) {
        super(codec);
        this.resourceLocationPool = resourceLocationPool;
        this.isNetherGraveyard = isNetherGraveyard;
    }
    public Graveyard(Codec<NoFeatureConfig> codec, String resourceLocationPool) {
        super(codec);
        this.resourceLocationPool = resourceLocationPool;
    }
    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Graveyard.Start::new;
    }
    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public class Start extends StructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }


        public void generatePieces(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
            int x = chunkX * 16;
            int z = chunkZ * 16;

            BlockPos centerPos = new BlockPos(x, 0 ,z);

            if (Graveyard.this.isNetherGraveyard) {
                IBlockReader blockReader = chunkGenerator.getBaseColumn(x, z);
                for (int y = 0; y < 256; y++) {
                            if (blockReader.getBlockState(new BlockPos(x, y, z)).isAir()) {
                                centerPos = new BlockPos(x, y, z);
                                break;
                            }
                }
            }

            JigsawManager.addPieces(
                    dynamicRegistryManager,
                    new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(new ResourceLocation(Main.MODID, Graveyard.this.resourceLocationPool)), 10),
                    AbstractVillagePiece::new,
                    chunkGenerator,
                    templateManagerIn,
                    centerPos,
                    this.pieces,
                    this.random,
                    false,
                    !Graveyard.this.isNetherGraveyard

            );

            Vector3i structureCenter = this.pieces.get(0).getBoundingBox().getCenter();
            int xOffset = centerPos.getX() - structureCenter.getX();
            int zOffset = centerPos.getZ() - structureCenter.getZ();
            for(StructurePiece structurePiece : this.pieces){
                structurePiece.move(xOffset, 0, zOffset);
            }

            this.calculateBoundingBox();


        }

    }
}
