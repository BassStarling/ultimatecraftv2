package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.blockentity.SparkCompressorBlockEntity;
import com.bassstarling.ultimatecraftv2.blockentity.SparkGeneratorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, UltimateCraftV2.MODID);

    public static final RegistryObject<BlockEntityType<SparkGeneratorBlockEntity>> SPARK_GENERATOR =
            BLOCK_ENTITIES.register("spark_generator",
                    () -> BlockEntityType.Builder.of(
                            SparkGeneratorBlockEntity::new,
                            ModBlocks.SPARK_GENERATOR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<SparkCompressorBlockEntity>> SPARK_COMPRESSOR =
            BLOCK_ENTITIES.register("spark_compressor",
                    () -> BlockEntityType.Builder.of(
                            SparkCompressorBlockEntity::new,
                            ModBlocks.SPARK_COMPRESSOR.get()
                    ).build(null));
}
