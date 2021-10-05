package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.mixin.StructuresConfigAccessor;
import net.fabricmc.example.structures.NoWaterProcessor;
import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ExampleMod implements ModInitializer {
	public static final String MODID = "example";
	public static final Identifier UNDERGROUNDVILLAGE_IDENTIFIER = new Identifier(ExampleMod.MODID, "underground_village");
	public static final Identifier CONFIGURED_UNDERGROUNDVILLAGE_IDENTIFIER = new Identifier(ExampleMod.MODID, "configured_underground_village");
	public static StructureProcessorType<NoWaterProcessor> NOWATER_PROCESSOR = () -> NoWaterProcessor.CODEC;

	private static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> OBSIDIAN_SURFACE_BUILDER = SurfaceBuilder.NETHER
			.withConfig(new TernarySurfaceConfig(
					Blocks.OBSIDIAN.getDefaultState(),
					Blocks.STONE.getDefaultState(),
					Blocks.GRAVEL.getDefaultState()));

	private static final Biome OBSILAND = createObsiland();

	private static Biome createObsiland() {
		// We specify what entities spawn and what features generate in the biome.
		// Aside from some structures, trees, rocks, plants and
		//   custom entities, these are mostly the same for each biome.
		// Vanilla configured features for biomes are defined in DefaultBiomeFeatures.

		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(spawnSettings);
		DefaultBiomeFeatures.addMonsters(spawnSettings, 95, 5, 100);

		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
		generationSettings.surfaceBuilder(OBSIDIAN_SURFACE_BUILDER);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
		DefaultBiomeFeatures.addLandCarvers(generationSettings);
		DefaultBiomeFeatures.addDefaultLakes(generationSettings);
		DefaultBiomeFeatures.addDungeons(generationSettings);
		DefaultBiomeFeatures.addMineables(generationSettings);
		DefaultBiomeFeatures.addDefaultOres(generationSettings);
		DefaultBiomeFeatures.addDefaultDisks(generationSettings);
		DefaultBiomeFeatures.addSprings(generationSettings);
		DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);

		return (new Biome.Builder())
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.NONE)
				.depth(0.125F)
				.scale(0.05F)
				.temperature(0.8F)
				.downfall(0.4F)
				.effects((new BiomeEffects.Builder())
						.waterColor(0x3f76e4)
						.waterFogColor(0x050533)
						.fogColor(0xc0d8ff)
						.skyColor(0x77adff)
						.build())
				.spawnSettings(spawnSettings.build())
				.generationSettings(generationSettings.build())
				.build();
	}
	public static final RegistryKey<Biome> OBSILAND_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("tutorial", "obsiland"));

	@Override
	public void onInitialize() {
		Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier("tutorial", "obsidian"), OBSIDIAN_SURFACE_BUILDER);
		Registry.register(BuiltinRegistries.BIOME, OBSILAND_KEY.getValue(), OBSILAND);

		OverworldBiomes.addContinentalBiome(OBSILAND_KEY, OverworldClimate.TEMPERATE, 2D);
		OverworldBiomes.addContinentalBiome(OBSILAND_KEY, OverworldClimate.COOL, 2D);


		ExStructures.registerStructureFeatures();
		ExConfiguredStructures.registerConfiguredStructures();
		Registry.register(Registry.STRUCTURE_PROCESSOR, new Identifier(MODID, "nowater_processor"), NOWATER_PROCESSOR);

		Predicate<BiomeSelectionContext> biomes = BiomeSelectors.categories(Biome.Category.FOREST, Biome.Category.JUNGLE, Biome.Category.DESERT, Biome.Category.PLAINS, Biome.Category.SAVANNA).and(BiomeSelectors.foundInOverworld());

		// Add structures to biomes.
		BiomeModifications.create(UNDERGROUNDVILLAGE_IDENTIFIER)
				.add(ModificationPhase.ADDITIONS,
						biomes,
						context -> context.getGenerationSettings().addBuiltInStructure(ExConfiguredStructures.CONFIGURED_UNDERGROUND_VILLAGE));




	}


}

