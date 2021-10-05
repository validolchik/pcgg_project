package net.fabricmc.example;

import net.fabricmc.example.structures.UnderGroundVillageStructure;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ExStructures {
    public static StructureFeature<DefaultFeatureConfig> UNDERGROUND_VILLAGE = new UnderGroundVillageStructure(DefaultFeatureConfig.CODEC);

    public static void registerStructureFeatures() {
        // Create structure config using config values.
        StructureConfig structureConfig = new StructureConfig(32, 8, 8699777);

        FabricStructureBuilder.create(ExampleMod.UNDERGROUNDVILLAGE_IDENTIFIER, UNDERGROUND_VILLAGE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(structureConfig)
                .superflatFeature(UNDERGROUND_VILLAGE.configure(FeatureConfig.DEFAULT))
                .register();
    }
}
