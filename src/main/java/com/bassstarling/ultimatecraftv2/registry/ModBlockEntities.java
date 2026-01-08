package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, UltimateCraftV2.MOD_ID);

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

    public static final RegistryObject<BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR =
            BLOCK_ENTITIES.register("heat_generator",
                    () -> BlockEntityType.Builder.of(
                            HeatGeneratorBlockEntity::new,
                            ModBlocks.HEAT_GENERATOR.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER =
            BLOCK_ENTITIES.register("crusher",
                    () -> BlockEntityType.Builder.of(
                            CrusherBlockEntity::new,
                            ModBlocks.CRUSHER_BLOCK.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<WashingMachineBlockEntity>> WASHING_MACHINE =
            BLOCK_ENTITIES.register("washing_machine",
                    () -> BlockEntityType.Builder.of(
                            WashingMachineBlockEntity::new,
                            ModBlocks.WASHING_MACHINE.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<EletricCalcinerBlockEntity>> ELETRIC_CALCINER =
            BLOCK_ENTITIES.register("eletric_calciner",
                    () -> BlockEntityType.Builder.of(
                            EletricCalcinerBlockEntity::new,
                            ModBlocks.ELETRICCALCINER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<ElectrolyticFurnaceBlockEntity>> ELECTROLYTIC_FURNACE =
            BLOCK_ENTITIES.register("electrolytic_furnace",
                    () -> BlockEntityType.Builder.of(
                            ElectrolyticFurnaceBlockEntity::new,
                            ModBlocks.ELECTROLYTICFURNACE.get()
                    ).build(null));
}