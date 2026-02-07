package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.init.ModTiers;
import com.bassstarling.ultimatecraftv2.item.PipeItem;
import com.bassstarling.ultimatecraftv2.item.SoBolt;
import com.bassstarling.ultimatecraftv2.item.SparkStone;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UltimateCraftV2.MOD_ID);

    public enum MoldType {
        PLATE("plate");

        private final String name;
        MoldType(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public static final RegistryObject<Item> ASSEMBLED_CYLINDER_BLOCK =
            ITEMS.register("assembled_cylinder_block",
                    () -> new Item(new Item.Properties()
                    ));

    public static final RegistryObject<Item> BOLT =
            ITEMS.register("bolt",
                    () -> new SoBolt(new Item.Properties().stacksTo(64)
                    ));
    public static final RegistryObject<Item> SPARK_STONE =
            ITEMS.register("spark_stone",
                    () -> new SparkStone(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WIRE =
            ITEMS.register("wire",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WEAK_MAGNET =
            ITEMS.register("weak_magnet",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SPARK_GENERATOR =
            ITEMS.register("spark_generator",
                    () -> new BlockItem(
                            ModBlocks.SPARK_GENERATOR.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.spark_generator.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.spark_generator.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> SPARK_COMPRESSOR =
            ITEMS.register("spark_compressor",
                    () -> new BlockItem(
                            ModBlocks.SPARK_COMPRESSOR.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.spark_compressor.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.spark_compressor.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> HEATE_GENERATOR =
            ITEMS.register("heat_generator",
                    () -> new BlockItem(
                            ModBlocks.HEAT_GENERATOR.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.heat_generator.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.heat_generator.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> IRON_PLATE =
            ITEMS.register("iron_plate",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COIL =
            ITEMS.register("coil",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> PROPELLER =
            ITEMS.register("propeller",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> RAW_BAUXITE =
            ITEMS.register("raw_bauxite",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> BAUXITE_ORE =
            ITEMS.register("bauxite_ore",
                    () -> new BlockItem(
                            ModBlocks.BAUXITE_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> COARSE_BAUXITE_POWDER =
            ITEMS.register("coarse_bauxite_powder",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRUSHER_BLOCK =
            ITEMS.register("crusher_block",
                    () -> new BlockItem(
                            ModBlocks.CRUSHER_BLOCK.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.crusher_block.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.crusher_block.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> WASHED_BAUXITE_POWDER =
            ITEMS.register("washed_bauxite_powder",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WASHING_MACHINE =
            ITEMS.register("washing_machine",
                    () -> new BlockItem(
                            ModBlocks.WASHING_MACHINE.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.washing_machine.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.washing_machine.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> ALUMINA =
            ITEMS.register("alumina",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELETRICCALCINER =
            ITEMS.register("eletriccalciner",
                    () -> new BlockItem(
                            ModBlocks.ELETRICCALCINER.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.eletriccalciner.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.eletriccalciner.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> IRON_WIRE =
            ITEMS.register("iron_wire",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> HEATER =
            ITEMS.register("heater",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRYOLITE_ORE =
            ITEMS.register("cryolite_ore",
                    () -> new BlockItem(
                            ModBlocks.CRYOLITE_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRYOLITE =
            ITEMS.register("cryolite",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELECTROLYTICFURNACE =
            ITEMS.register("electrolyticfurnace",
                    () -> new BlockItem(
                            ModBlocks.ELECTROLYTICFURNACE.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.electrolyticfurnace.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.electrolyticfurnace.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> MOLTEN_CRYOLITE =
            ITEMS.register("molten_cryolite",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> MOLTEN_CRYOLITE_WITH_ALUMINA =
            ITEMS.register("molten_cryolite_with_alumina",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> USED_ELECTROLYTICFURNACE =
            ITEMS.register("used_electrolyticfurnace",
                    () -> new BlockItem(
                            ModBlocks.USED_ELECTROLYTICFURNACE.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.used_electrolyticfurnace.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.used_electrolyticfurnace.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> ALUMINIUM_INGOT =
            ITEMS.register("aluminium_ingot",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> MOLTEN_ALUMINIUM_IN_BUCKET =
            ITEMS.register("molten_aluminium_in_bucket",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> PIPE = ITEMS.register("pipe",
            () -> new PipeItem(
                    ModTiers.PIPE,  // 上で作った素材 (耐久500)
                    4,              // 追加攻撃力: 4 (合計: 1+2+5 = 7ダメージ = ハート3.5個分)
                    -1.6f,          // 攻撃速度補正: -1.6 (合計: 4.0 - 1.6 = 2.4)
                    new Item.Properties()
            ));
    public static final RegistryObject<Item> INDUSTRIAL_WORKBENCH =
            ITEMS.register("industrial_workbench",
                    () -> new BlockItem(
                            ModBlocks.INDUSTRIAL_WORKBENCH.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> TAR_BUCKET =
            ITEMS.register("tar_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                            .stacksTo(1)
                    ));
    public static final RegistryObject<Item> IRON_PIPE =
            ITEMS.register("iron_pipe",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKE =
            ITEMS.register("coke",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKEOVEN =
            ITEMS.register("cokeoven",
                    () -> new BlockItem(
                            ModBlocks.COKEOVEN.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKE_DUST =
            ITEMS.register("coke_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> UNFIRED_ELECTRODE =
            ITEMS.register("unfired_electrode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> COKE_ELECTRODE =
            ITEMS.register("coke_electrode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> GRAPHITE_ELECTRODE =
            ITEMS.register("graphite_electrode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ARC_FURNACE =
            ITEMS.register("arc_furnace",
                    () -> new BlockItem(
                            ModBlocks.ARC_FURNACE.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.arc_furnace.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.arc_furnace.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> PIG_IRON =
            ITEMS.register("pig_iron",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> UNFIRED_PIG_IRON =
            ITEMS.register("unfired_pig_iron",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> STEEL_INGOT =
            ITEMS.register("steel_ingot",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELECTROLYZER =
            ITEMS.register("electrolyzer",
                    () -> new BlockItem(
                            ModBlocks.ELECTROLYZER.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.electrolyzer.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.electrolyzer.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> OXYGEN_BOTTLE =
            ITEMS.register("oxygen_bottle",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> OXYGEN_CONVERTER =
            ITEMS.register("oxygen_converter",
                    () -> new BlockItem(
                            ModBlocks.OXYGEN_CONVERTER.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.oxygen_converter.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.oxygen_converter.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> CASTING_MACHINE =
            ITEMS.register("casting_machine",
                    () -> new BlockItem(
                            ModBlocks.CASTING_MACHINE.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.casting_machine.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.casting_machine.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> STEEL_PLATE =
            ITEMS.register("steel_plate",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ALUMINIUM_PLATE =
            ITEMS.register("aluminium_plate",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> HIGH_PURITY_ALUMINA =
            ITEMS.register("high_purity_alumina",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SINTERED_CERAMIC =
            ITEMS.register("sintered_ceramic",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> POWER_SUPPLY =
            ITEMS.register("power_supply",
                    () -> new Item(new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.power_supply.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.power_supply.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> WELDING_MACHINE =
            ITEMS.register("welding_machine",
                    () -> new Item(new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.welding_machine.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.welding_machine.usage")
                                        .withStyle(ChatFormatting.GRAY));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.wiki")
                                        .withStyle(ChatFormatting.YELLOW));
                            } else {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.hold_shift")
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> HAMMER =
            ITEMS.register("hammer",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> FORMED_CERAMIC =
            ITEMS.register("formed_ceramic",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ADDITIVE_PROCESSED_SINTERED_CERAMIC =
            ITEMS.register("additive_processed_sintered_ceramic",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ELECTROLYTIC_LAYER =
            ITEMS.register("electrolytic_layer",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CATHODE =
            ITEMS.register("cathode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ANODE =
            ITEMS.register("anode",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SALT_BLOCK =
            ITEMS.register("salt_block",
                    () -> new BlockItem(
                            ModBlocks.SALT_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> LIMESTONE =
            ITEMS.register("limestone",
                    () -> new BlockItem(
                            ModBlocks.LIMESTONE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> SALT =
            ITEMS.register("salt",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> QUICK_LIME =
            ITEMS.register("quick_lime",
                    () -> new Item(new Item.Properties()) {
                        @Override
                        public InteractionResult useOn(UseOnContext context) {
                            Level level = context.getLevel();
                            BlockPos pos = context.getClickedPos();
                            BlockState state = level.getBlockState(pos);
                            Player player = context.getPlayer();

                            // 水源ブロック（あるいは水を含むブロック）を右クリックしたか判定
                            if (state.getFluidState().is(FluidTags.WATER)) {
                                if (!level.isClientSide) {
                                    // 1. 生石灰を消費
                                    context.getItemInHand().shrink(1);

                                    // 2. 消石灰をドロップ
                                    ItemStack result = new ItemStack(ModItems.SLAKED_LIME.get());
                                    ItemHandlerHelper.giveItemToPlayer(player, result);

                                    // 3. 演出（湯気とジュッという音）
                                    level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    ((ServerLevel) level).sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.05);
                                }
                                return InteractionResult.sidedSuccess(level.isClientSide);
                            }
                            return InteractionResult.PASS;
                        }
                    });
    public static final RegistryObject<Item> SLAKED_LIME =
            ITEMS.register("slaked_lime",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> BRINE_BOTTLE =
            ITEMS.register("brine_bottle",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
            ));
    public static final RegistryObject<Item> SODIUM_HYDROXIDE_SOLUTION_BOTTLE =
            ITEMS.register("sodium_hydroxide_solution_bottle",
                    () -> new Item(new Item.Properties()
                            .stacksTo(1)){@Override
                    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
                        ItemStack itemstack = pPlayer.getItemInHand(pHand);

                        // サーバーサイドでのみ処理を行う
                        if (!pLevel.isClientSide) {
                            // 1. パーティクルや音の演出（空気を吸い込んでいるような演出）
                            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                                    SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.5F, 1.0F);

                            // 2. 新しいアイテム（炭酸水素ナトリウム溶液入り瓶）の作成
                            ItemStack resultStack = new ItemStack(ModItems.SODIUM_BICARBONATE_SOLUTION_BOTTLE.get());

                            // 3. アイテムの消費と付与（クリエイティブモードを考慮）
                            if (!pPlayer.getAbilities().instabuild) {
                                itemstack.shrink(1);
                            }

                            if (itemstack.isEmpty()) {
                                return InteractionResultHolder.success(resultStack);
                            } else {
                                if (!pPlayer.getInventory().add(resultStack)) {
                                    pPlayer.drop(resultStack, false);
                                }
                            }
                        }

                        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
                    }}
                    );
    public static final RegistryObject<Item> SODIUM_BICARBONATE_SOLUTION_BOTTLE =
            ITEMS.register("sodium_bicarbonate_solution_bottle",
                    () -> new Item(new Item.Properties()
                            .stacksTo(1)
                    ));
    public static final RegistryObject<Item> SODIUM_CARBONATE_SOLUTION_BOTTLE =
            ITEMS.register("sodium_carbonate_solution_bottle",
                    () -> new Item(new Item.Properties()
                            .stacksTo(1)
                            .craftRemainder(Items.GLASS_BOTTLE)
                    ));
    public static final RegistryObject<Item> FOAMED_ALUMINA =
            ITEMS.register("foamed_alumina",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> POROUS_INSULATION_BLOCK =
            ITEMS.register("porous_insulation_block",
                    () -> new BlockItem(
                            ModBlocks.POROUS_INSULATION_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> NICKEL_ORE =
            ITEMS.register("nickel_ore",
                    () -> new BlockItem(
                            ModBlocks.NICKEL_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> DEEPSLATE_NICKEL_ORE =
            ITEMS.register("deepslate_nickel_ore",
                    () -> new BlockItem(
                            ModBlocks.DEEPSLATE_NICKEL_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> RAW_NICKEL =
            ITEMS.register("raw_nickel",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> NICKEL_INGOT =
            ITEMS.register("nickel_ingot",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> THERMAL_CONTROL_CIRCUIT =
            ITEMS.register("thermal_control_circuit",
                    () -> new Item(new Item.Properties()
                    ));

    // --- 型（Mold）の自動登録システム ---
    // 生成されたアイテムを保存しておくMap（後で他からアクセスするため）
    public static final Map<MoldType, RegistryObject<Item>> MOLDS = new HashMap<>();

    static {
        for (MoldType type : MoldType.values()) {
            String id = "mold_" + type.getName(); // 例: mold_plate, mold_gear

            RegistryObject<Item> moldItem = ITEMS.register(id,
                    () -> new Item(new Item.Properties().stacksTo(1))); // 型はスタックしない設定など

            MOLDS.put(type, moldItem);
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
//電解機の材料に必要とする電極でチタンやプラチナを使用するらしい。
//プラチナのレア度はダイヤの4万倍ほど。