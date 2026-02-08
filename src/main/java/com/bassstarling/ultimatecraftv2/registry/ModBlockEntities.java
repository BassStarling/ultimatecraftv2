package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.blockentity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
    public static final RegistryObject<BlockEntityType<IndustrialWorkbenchBlockEntity>> INDUSTRIAL_WORKBENCH =
            BLOCK_ENTITIES.register("industrial_workbench",
                    () -> BlockEntityType.Builder.of(
                            IndustrialWorkbenchBlockEntity::new,
                            ModBlocks.INDUSTRIAL_WORKBENCH.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<CokeOvenBlockEntity>> COKE_OVEN =
            BLOCK_ENTITIES.register("coke_oven",
                    () -> BlockEntityType.Builder.of(
                            CokeOvenBlockEntity::new,
                            ModBlocks.COKEOVEN.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<ArcFurnaceBlockEntity>> ARC__FURNACE =
            BLOCK_ENTITIES.register("arc__furnace",
                    () -> BlockEntityType.Builder.of(
                            ArcFurnaceBlockEntity::new,
                            ModBlocks.ARC_FURNACE.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<DisposableArcFurnaceBlockEntity>> DISPOSABLE_ARC__FURNACE =
            BLOCK_ENTITIES.register("disposable_arc__furnace",
                    () -> BlockEntityType.Builder.of(
                            DisposableArcFurnaceBlockEntity::new,
                            ModBlocks.DISPOSABLE_ARC_FURNACE.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<ElectrolyzerBlockEntity>> ELECTROLYZER_ =
            BLOCK_ENTITIES.register("electrolyzer_",
                    () -> BlockEntityType.Builder.of(
                            ElectrolyzerBlockEntity::new,
                            ModBlocks.ELECTROLYZER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<OxygenConverterBlockEntity>> OXYGEN_CONVERTER_BE =
            BLOCK_ENTITIES.register("oxygen_converter_be",
                    () -> BlockEntityType.Builder.of(
                            OxygenConverterBlockEntity::new,
                            ModBlocks.OXYGEN_CONVERTER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<CastingMachineBlockEntity>> CASTING_MACHINE_BE =
            BLOCK_ENTITIES.register("casting_machine_be",
                    () -> BlockEntityType.Builder.of(
                            CastingMachineBlockEntity::new,
                            ModBlocks.CASTING_MACHINE.get()
                    ).build(null));
}