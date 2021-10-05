package net.fabricmc.example;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
//	public static final Logger LOGGER = LogManager.getLogger("modid");
	// SurfaceBuilder defines how the surface of your biome looks.
	// We use custom surface builder for our biome to cover surface with obsidians.
	private static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> OBSIDIAN_SURFACE_BUILDER = SurfaceBuilder.DEFAULT
			.withConfig(new TernarySurfaceConfig(
					Blocks.SAND.getDefaultState(),
					Blocks.SANDSTONE.getDefaultState(),
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
		DefaultBiomeFeatures.addDesertDeadBushes(generationSettings);

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
	private static final Feature<SpiralFeatureConfig> SPIRAL = new SpiralFeature(SpiralFeatureConfig.CODEC);

	public static final ConfiguredFeature<?, ?> STONE_SPIRAL = SPIRAL.configure(new SpiralFeatureConfig(ConstantIntProvider.create(15), new SimpleBlockStateProvider(Blocks.STONE.getDefaultState())))
			.decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR_WG)))
			.spreadHorizontally()
			.applyChance(5);

	@Override
	public void onInitialize() {
		Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier("tutorial", "obsidian"), OBSIDIAN_SURFACE_BUILDER);
		Registry.register(BuiltinRegistries.BIOME, OBSILAND_KEY.getValue(), OBSILAND);

		OverworldBiomes.addContinentalBiome(OBSILAND_KEY, OverworldClimate.TEMPERATE, 2D);
		OverworldBiomes.addContinentalBiome(OBSILAND_KEY, OverworldClimate.COOL, 2D);
		Registry.register(Registry.FEATURE, new Identifier("tutorial", "spiral"), SPIRAL);

		RegistryKey<ConfiguredFeature<?, ?>> stoneSpiral = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
				new Identifier("tutorial", "stone_spiral"));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, stoneSpiral.getValue(), STONE_SPIRAL);
		BiomeModifications.addFeature(BiomeSelectors.includeByKey(OBSILAND_KEY), GenerationStep.Feature.UNDERGROUND_ORES, stoneSpiral);
	}
}