package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.block.SparkCommpressorBlock;
import com.bassstarling.ultimatecraftv2.block.SparkGeneratorBlock;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, UltimateCraftV2.MODID);

    public static final RegistryObject<Block> SPARK_GENERATOR =
            BLOCKS.register("spark_generator",
                    () -> new SparkGeneratorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F)
                                    .sound(SoundType.METAL)
                    )
            );
    public static final RegistryObject<Block> SPARK_COMPRESSOR =
            BLOCKS.register("spark_compressor",
                    () -> new SparkCommpressorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2.0F)
                                    .requiresCorrectToolForDrops()
                    ));
}
