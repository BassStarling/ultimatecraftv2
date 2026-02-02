package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, UltimateCraftV2.MOD_ID);

    public static final RegistryObject<Block> SPARK_GENERATOR =
            BLOCKS.register("spark_generator",
                    () -> new SparkGeneratorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0F)
                                    .sound(SoundType.METAL)
                                    .requiresCorrectToolForDrops()
                    )
            );
    public static final RegistryObject<Block> SPARK_COMPRESSOR =
            BLOCKS.register("spark_compressor",
                    () -> new SparkCompressorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2.0F)
                                    .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> HEAT_GENERATOR =
            BLOCKS.register("heat_generator",
                    () -> new HeatGeneratorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2.0F)
                                    .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> BAUXITE_ORE =
            BLOCKS.register("bauxite_ore",
                    () -> new Block(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
                    ));
    public static final RegistryObject<Block> CRUSHER_BLOCK =
            BLOCKS.register("crusher_block",
                    () -> new CrusherBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2.0F)
                                    .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> WASHING_MACHINE =
            BLOCKS.register("washing_machine",
                    () -> new WashingMachineBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2.0F)
                                    .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> ELETRICCALCINER =
            BLOCKS.register("eletriccalciner",
                    () -> new EletricCalcinerBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2.0F)
                                    .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> CRYOLITE_ORE =
            BLOCKS.register("cryolite_ore",
                    () -> new Block(BlockBehaviour.Properties
                            .of()
                            .mapColor(MapColor.STONE)
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> ELECTROLYTICFURNACE =
            BLOCKS.register("electrolyticfurnace",
                    () -> new ElectrolyticFurnaceBlock(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));

    public static final RegistryObject<Block> USED_ELECTROLYTICFURNACE =
            BLOCKS.register("used_electrolyticfurnace",
                    () -> new UsedEF(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> INDUSTRIAL_WORKBENCH =
            BLOCKS.register("industrial_workbench",
                    () -> new IWBenchBlock(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> COKEOVEN =
            BLOCKS.register("cokeoven",
                    () -> new CokeOvenBlock(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> ARC_FURNACE =
            BLOCKS.register("arc_furnace",
                    () -> new ArcFurnaceBlock(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> ELECTROLYZER =
            BLOCKS.register("electrolyzer",
                    () -> new ElectrolyzerBlock(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> OXYGEN_CONVERTER =
            BLOCKS.register("oxygen_converter",
                    () -> new OxygenConverterBlock(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
    public static final RegistryObject<Block> CASTING_MACHINE =
            BLOCKS.register("casting_machine",
                    () -> new CastingMachineBlock(BlockBehaviour.Properties
                            .of()
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                    ));
}