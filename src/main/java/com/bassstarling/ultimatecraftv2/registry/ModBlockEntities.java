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
    public static final RegistryObject<BlockEntityType<DigesterBlockEntity>> DIGESTER_BE =
            BLOCK_ENTITIES.register("digester_be",
                    () -> BlockEntityType.Builder.of(
                            DigesterBlockEntity::new,
                            ModBlocks.DIGESTER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<FilterBlockEntity>> FILTERBE =
            BLOCK_ENTITIES.register("filterbe",
                    () -> BlockEntityType.Builder.of(
                            FilterBlockEntity::new,
                            ModBlocks.FILTER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<CrystallizerBlockEntity>> CRYSTALLIZER_BE =
            BLOCK_ENTITIES.register("crystallizer_be",
                    () -> BlockEntityType.Builder.of(
                            CrystallizerBlockEntity::new,
                            ModBlocks.CRYSTALLIZER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<UniversalElectrolyzerBlockEntity>> UNIVERSAL_ELECTROLYZER_BE =
            BLOCK_ENTITIES.register("universal_electrolyzer_be",
                    () -> BlockEntityType.Builder.of(
                            UniversalElectrolyzerBlockEntity::new,
                            ModBlocks.UNIVERSAL_ELECTROLYZER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<PumpBlockEntity>> PUMP_BE =
            BLOCK_ENTITIES.register("pump_be",
                    () -> BlockEntityType.Builder.of(
                            PumpBlockEntity::new,
                            ModBlocks.PUMP.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<ThrowerBlockEntity>> THROWER_BE =
            BLOCK_ENTITIES.register("thrower_be",
                    () -> BlockEntityType.Builder.of(
                            ThrowerBlockEntity::new,
                            ModBlocks.THROWER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<ReppordBlockEntity>> REPPORD_BE =
            BLOCK_ENTITIES.register("reppord_be",
                    () -> BlockEntityType.Builder.of(
                            ReppordBlockEntity::new,
                            ModBlocks.REPPORD.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<AutoCasterBlockEntity>> AUTO_CASTER_BE =
            BLOCK_ENTITIES.register("auto_caster_be",
                    () -> BlockEntityType.Builder.of(
                            AutoCasterBlockEntity::new,
                            ModBlocks.AUTOCASTER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<DistillerBlockEntity>> DISTILLER_BE =
            BLOCK_ENTITIES.register("distiller_be",
                    () -> BlockEntityType.Builder.of(
                            DistillerBlockEntity::new,
                            ModBlocks.DISTILLER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<NaphthaCauldronBlockEntity>> NAPHTHA_CAULDRON =
            BLOCK_ENTITIES.register("naphtha_cauldron",
                    () -> BlockEntityType.Builder.of(
                            NaphthaCauldronBlockEntity::new,
                            ModBlocks.NAPHTHA_CAULDRON.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<BatteryChargerBlockEntity>> BATTERY_CHARGER =
            BLOCK_ENTITIES.register("battery_charger",
                    () -> BlockEntityType.Builder.of(
                            BatteryChargerBlockEntity::new,
                            ModBlocks.BATTERYCHARGER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<AgitatedTankRecrystallizerBlockEntity>> AGITATEDTANK_RECRYSTALLIZER =
            BLOCK_ENTITIES.register("agitatedtank_recrystallizer",
                    () -> BlockEntityType.Builder.of(
                            AgitatedTankRecrystallizerBlockEntity::new,
                            ModBlocks.AGITATEDTANKRECRYSTALLIZER.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<DustCollectorBlockEntity>> DUST_COLLECTOR =
            BLOCK_ENTITIES.register("dust_collector",
                    () -> BlockEntityType.Builder.of(
                            DustCollectorBlockEntity::new,
                        ModBlocks.DUSTCOLLECTOR.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<SinteringFurnaceBlockEntity>> SINTERING_FURNACE =
            BLOCK_ENTITIES.register("sintering_furnace",
                    () -> BlockEntityType.Builder.of(
                            SinteringFurnaceBlockEntity::new,
                            ModBlocks.SINTERINGFURNACE.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<EthylenePlantBlockEntity>> ETHYLENE__PLANT =
            BLOCK_ENTITIES.register("ethylene__plant",
                    () -> BlockEntityType.Builder.of(
                            EthylenePlantBlockEntity::new,
                            ModBlocks.ETHYLENE_PLANT.get()
                    ).build(null));
}