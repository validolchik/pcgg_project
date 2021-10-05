package net.fabricmc.example.structures;

import com.mojang.serialization.Codec;
import net.fabricmc.example.ExampleMod;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class UnderGroundVillageStructure extends StructureFeature<DefaultFeatureConfig> {
    public static Identifier START_POOL = new Identifier(ExampleMod.MODID, "start_pool");

    public UnderGroundVillageStructure(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return UnderGroundVillageStructure.Start::new;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long worldSeed, ChunkRandom random, ChunkPos pos, Biome biome, ChunkPos chunkPos, DefaultFeatureConfig config, HeightLimitView world) {
        int terrainHeight = chunkGenerator.getHeightOnGround(pos.x << 4, pos.z << 4, Heightmap.Type.WORLD_SURFACE_WG, world);
        int maxHeight = chunkGenerator.getSeaLevel() + 8;

        return terrainHeight <= maxHeight;
    }

    public static class Start extends MarginedStructureStart<DefaultFeatureConfig> {
        private static StructurePoolFeatureConfig structurePoolFeatureConfig = null;

        public Start(StructureFeature<DefaultFeatureConfig> structureIn, ChunkPos pos, int referenceIn, long seedIn) {
            super(structureIn, pos, referenceIn, seedIn);
        }

        @Override
        public void init(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, StructureManager manager, ChunkPos pos, Biome biome, DefaultFeatureConfig config, HeightLimitView world) {

            int x = pos.x << 4;
            int z = pos.z << 4;

            BlockPos blockPos = new BlockPos(x, 0, z);


            if (structurePoolFeatureConfig == null)
                structurePoolFeatureConfig = new StructurePoolFeatureConfig(() -> registryManager.get(Registry.STRUCTURE_POOL_KEY).get(START_POOL), 10);

            StructurePoolBasedGenerator.generate(registryManager,
                    structurePoolFeatureConfig,
                    PoolStructurePiece::new, chunkGenerator, manager, blockPos, this, this.random, false, true, world);

            /* TODO: Attempt to remove waterlogging from all blocks inside.*/
            this.children.forEach(structurePiece -> {
                structurePiece.translate(0, 1, 0);
            });

            this.setBoundingBoxFromChildren();
        }
    }
}
