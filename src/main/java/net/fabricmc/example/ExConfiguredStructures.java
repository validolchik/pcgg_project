package net.fabricmc.example;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class ExConfiguredStructures {
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_UNDERGROUND_VILLAGE = ExStructures.UNDERGROUND_VILLAGE.configure(DefaultFeatureConfig.DEFAULT);

    public static void registerConfiguredStructures() {
        Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, ExampleMod.CONFIGURED_UNDERGROUNDVILLAGE_IDENTIFIER, CONFIGURED_UNDERGROUND_VILLAGE);
    }
}
