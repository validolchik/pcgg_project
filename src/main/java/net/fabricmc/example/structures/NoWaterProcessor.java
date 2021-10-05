package net.fabricmc.example.structures;

import com.mojang.serialization.Codec;
import net.fabricmc.example.ExampleMod;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public class NoWaterProcessor extends StructureProcessor {
    public static final Codec<NoWaterProcessor> CODEC = Codec.unit(NoWaterProcessor::new);

    @Nullable
    @Override
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo structureBlockInfoLocal, Structure.StructureBlockInfo structureBlockInfoWorld, StructurePlacementData data) {
        Chunk chunk = world.getChunk(structureBlockInfoWorld.pos);

        if(structureBlockInfoWorld.state.contains(Properties.WATERLOGGED) && !chunk.getFluidState(structureBlockInfoWorld.pos).isEmpty())
        {
            boolean waterlog = (structureBlockInfoLocal.state.contains(Properties.WATERLOGGED) && structureBlockInfoLocal.state.get(Properties.WATERLOGGED));

            chunk.setBlockState(structureBlockInfoWorld.pos, structureBlockInfoWorld.state.rotate(data.getRotation()).with(Properties.WATERLOGGED, waterlog), false);
        }

        return structureBlockInfoWorld;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ExampleMod.NOWATER_PROCESSOR;
    }
}
