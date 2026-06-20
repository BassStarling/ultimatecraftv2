package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.init.ModTiers;
import com.bassstarling.ultimatecraftv2.item.*;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
        INGOT("ingot"),
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
                    () -> new Item(new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            tooltip.add(Component.translatable("tooltip.ultimatecraftv2.molten_cryolite_with_alumina")
                                    .withStyle(ChatFormatting.YELLOW));
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> MOLTEN_CRYOLITE_WITH_ALUMINA =
            ITEMS.register("molten_cryolite_with_alumina",
                    () -> new Item(new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.molten_cryolite_with_alumina")
                                        .withStyle(ChatFormatting.YELLOW));
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
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
                    () -> new TarBucketItem(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                            .stacksTo(1)
                    )
                    {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            tooltip.add(Component.translatable("tooltip.ultimatecraftv2.tar_bucket")
                                    .withStyle(ChatFormatting.YELLOW));
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    }
            );
    public static final RegistryObject<Item> SODIUM_HYDROXIDE_BUCKET =
            ITEMS.register("sodium_hydroxide_bucket",
                    () -> new TarBucketItem(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                            .stacksTo(1)
                    ));
    public static final RegistryObject<Item> SODIUM_ALUMINATE_SUSPENSION_BUCKET =
            ITEMS.register("sodium_aluminate_suspension_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                            .stacksTo(1)
                    ));
    public static final RegistryObject<Item> SODIUM_ALUMINATE_BUCKET =
            ITEMS.register("sodium_aluminate_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                            .stacksTo(1)
                    ));
    public static final RegistryObject<Item> RED_MUD_BUCKET =
            ITEMS.register("red_mud_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                            .stacksTo(1)
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
    public static final RegistryObject<Item> CONVERTER =
            ITEMS.register("converter",
                    () -> new BlockItem(
                            ModBlocks.CONVERTER.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            // シフトキーを押している間だけ詳細を表示する設定
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.converter.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.converter.usage")
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
                        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                            ItemStack itemstack = player.getItemInHand(hand);
                            // 視線の先にある「液体」を探す（重要！）
                            BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

                            if (hitResult.getType() == HitResult.Type.BLOCK) {
                                BlockPos pos = hitResult.getBlockPos();
                                BlockState state = level.getBlockState(pos);

                                // 水だったら反応開始
                                if (state.getFluidState().is(FluidTags.WATER)) {
                                    if (!level.isClientSide) {
                                        // 1. 消費
                                        itemstack.shrink(1);

                                        // 2. 消石灰を付与
                                        ItemStack result = new ItemStack(ModItems.SLAKED_LIME.get());
                                        ItemHandlerHelper.giveItemToPlayer(player, result);

                                        // 3. 演出（音とパーティクル）
                                        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                                        ((ServerLevel) level).sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.05);
                                    }
                                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                                }
                            }
                            return InteractionResultHolder.pass(itemstack);
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
    public static final RegistryObject<Item> DISPOSABLE_ARC_FURNACE =
            ITEMS.register("disposable_arc_furnace",
                    () -> new DisposableArcFurnaceBlockItem(
                            ModBlocks.DISPOSABLE_ARC_FURNACE.get(),
                            new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            if (Screen.hasShiftDown()) {
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.disposable_arc_furnace.details")
                                        .withStyle(ChatFormatting.AQUA));
                                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.disposable_arc_furnace.usage")
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
    public static final RegistryObject<Item> STEEL_BLOCK =
            ITEMS.register("steel_block",
                    () -> new BlockItem(
                            ModBlocks.STEEL_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> ALUMINIUM_BLOCK =
            ITEMS.register("aluminium_block",
                    () -> new BlockItem(
                            ModBlocks.ALUMINIUM_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> NICKEL_BLOCK =
            ITEMS.register("nickel_block",
                    () -> new BlockItem(
                            ModBlocks.NICKEL_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> DIGESTER =
            ITEMS.register("digester",
                    () -> new BlockItem(
                            ModBlocks.DIGESTER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> FIRE_CLAY_BALL =
            ITEMS.register("fire_clay_ball",
            () -> new Item(new Item.Properties()
            ));
    public static final RegistryObject<Item> FIREBRICK =
            ITEMS.register("firebrick",
            () -> new Item(new Item.Properties()
            ));
    public static final RegistryObject<Item> RED_MUD_CHUNK =
            ITEMS.register("red_mud_chunk",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.IRON_NUGGET)
                    ));
    public static final RegistryObject<Item> SILICA =
            ITEMS.register("silica",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> FILTER =
            ITEMS.register("filter",
                    () -> new BlockItem(
                            ModBlocks.FILTER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRYSTALLIZER =
            ITEMS.register("crystallizer",
                    () -> new BlockItem(
                            ModBlocks.CRYSTALLIZER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> UNIVERSAL_ELECTROLYZER =
            ITEMS.register("universal_electrolyzer",
                    () -> new BlockItem(
                            ModBlocks.UNIVERSAL_ELECTROLYZER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> WHITE_FLUFFY_SOLID_OF_ALUMINIUM_HYDROXIDE =
            ITEMS.register("white_fluffy_solid_of_aluminum_hydroxide",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> PUMP =
            ITEMS.register("pump",
                    () -> new BlockItem(
                            ModBlocks.PUMP.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> THROWER =
            ITEMS.register("thrower",
                    () -> new BlockItem(
                            ModBlocks.THROWER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> REPPORD =
            ITEMS.register("reppord",
                    () -> new BlockItem(
                            ModBlocks.REPPORD.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> AUTOCASTER =
            ITEMS.register("autocaster",
                    () -> new BlockItem(
                            ModBlocks.AUTOCASTER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> ALUMINIUM_WIRE =
            ITEMS.register("aluminium_wire",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> PRESSURE_GAUGE =
            ITEMS.register("pressure_gauge",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> STEEL_TANK =
            ITEMS.register("steel_tank",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> VALVE =
            ITEMS.register("valve",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SULFUR_ORE =
            ITEMS.register("sulfur_ore",
                    () -> new BlockItem(
                            ModBlocks.SULFUR_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> HAZMAT_MASK =
            ITEMS.register("hazmat_mask",
            () -> new ArmorItem(ModArmorMaterials.HAZMAT, ArmorItem.Type.HELMET,
                    new Item.Properties()
            ));
    public static final RegistryObject<Item> HAZMAT_SUIT =
            ITEMS.register("hazmat_suit",
            () -> new ArmorItem(
                    ModArmorMaterials.HAZMAT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()
            ));
    public static final RegistryObject<Item> HAZMAT_LEGGINGS =
            ITEMS.register("hazmat_leggings",
            () -> new ArmorItem(
                    ModArmorMaterials.HAZMAT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties()
            ));
    public static final RegistryObject<Item> HAZMAT_BOOTS =
            ITEMS.register("hazmat_boots",
            () -> new ArmorItem(
                    ModArmorMaterials.HAZMAT, ArmorItem.Type.BOOTS,
                    new Item.Properties()
            ));
    public static final RegistryObject<Item> BENZENE_BUCKET =
            ITEMS.register("benzene_bucket",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> NAPHTHA_BUCKET =
            ITEMS.register("naphtha_bucket",
                    () -> new NaphthaBucketItem(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WASTE_OIL_BUCKET =
            ITEMS.register("waste_oil_bucket",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> DISTILLER =
            ITEMS.register("distiller",
                    () -> new BlockItem(
                            ModBlocks.DISTILLER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> SULFUR_DUST =
            ITEMS.register("sulfur_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> LITHIUM_ORE =
            ITEMS.register("lithium_ore",
                    () -> new BlockItem(
                            ModBlocks.LITHIUM_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> LITHIUM_CRYSTAL =
            ITEMS.register("lithium_crystal",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> NAPHTHA_CAULDRON =
            ITEMS.register("naphtha_cauldron",
                    () -> new BlockItem(
                            ModBlocks.NAPHTHA_CAULDRON.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> RUBBER_SHEET =
            ITEMS.register("rubber_sheet",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CHARCOAL_DUST =
            ITEMS.register("charcoal_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ACTIVATED_CARBON =
            ITEMS.register("activated_carbon",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> DUST_MASK =
            ITEMS.register("dust_mask",
                    () -> new ArmorItem(ModArmorMaterials.DUST, ArmorItem.Type.HELMET,
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> RUBBER_BALL =
            ITEMS.register("rubber_ball",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> NON_WOVEN_FABRIC =
            ITEMS.register("non_woven_fabric",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> RAW_RUBBER_BLOCK =
            ITEMS.register("raw_rubber_block",
                    () -> new BlockItem(
                            ModBlocks.RAW_RUBBER_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> RUBBER_BLOCK =
            ITEMS.register("rubber_block",
                    () -> new BlockItem(
                            ModBlocks.RUBBER_BLOCK.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> CINNABAR =
            ITEMS.register("cinnabar",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> CINNABAR_ORE =
            ITEMS.register("cinnabar_ore",
                    () -> new BlockItem(
                            ModBlocks.CINNABAR_ORE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> IRON_CONTAINER =
            ITEMS.register("iron_container",
                    () -> new Item(new Item.Properties()
                            .stacksTo(16)
                    ));
    public static final RegistryObject<Item> MERCURY_CONTAINER =
            ITEMS.register("mercy_container",
                    () -> new Item(new Item.Properties()
                            .stacksTo(1)
                        .craftRemainder(ModItems.IRON_CONTAINER.get())
                    ));
    public static final RegistryObject<Item> LITHIUM_ION_BATTERY =
            ITEMS.register("lithium_ion_battery",
                    () -> new LithiumIonBatteryItem(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SLIDE_SWITCH =
            ITEMS.register("slide_switch",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> UV_LAMP_UNIT =
            ITEMS.register("uv_lamp_unit",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> UV_LIGHT =
            ITEMS.register("uv_light",
                    () -> new UVLightItem(new Item.Properties()
                    ));
    public static final RegistryObject<Item> BATTERYCHARGER =
            ITEMS.register("batterycharger",
                    () -> new BlockItem(
                            ModBlocks.BATTERYCHARGER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> SCHEELITE =
            ITEMS.register("scheelite",
                    () -> new BlockItem(
                            ModBlocks.SCHEELITE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> SULFUR_DIOXIDE_DUST =
            ITEMS.register("sulfur_dioxide_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SULFURIC_ACID_BUCKET =
            ITEMS.register("sulfuric_acid_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                    ));
    public static final RegistryObject<Item> TUNGSTIC_ACID_BUCKET =
            ITEMS.register("tungstic_acid_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                    ));
    public static final RegistryObject<Item> TUNGSTEN_OXIDE_BUCKET =
            ITEMS.register("tungsten_oxide_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                    ));
    public static final RegistryObject<Item> TUNGSTEN_BUCKET =
            ITEMS.register("tungsten_bucket",
                    () -> new Item(new Item.Properties()
                            .craftRemainder(Items.BUCKET) // クラフト後に空バケツを返す設定
                    ));
    public static final RegistryObject<Item> TUNGSTEN_LUMP =
            ITEMS.register("tungsten_lump",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> TUNGSTEN_INGOT =
            ITEMS.register("tungsten_ingot",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> TUNGSTEN_FILAMENT =
            ITEMS.register("tungsten_filament",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> AGITATEDTANKRECRYSTALLIZERR =
            ITEMS.register("agitatedtankrecrystallizer",
                    () -> new BlockItem(
                            ModBlocks.AGITATEDTANKRECRYSTALLIZER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> MEMO =
            ITEMS.register("memo",
                    () -> new MemoItem(new Item.Properties()){
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                            tooltip.add(Component.translatable("tooltip.ultimatecraftv2.memo")
                                    .withStyle(ChatFormatting.YELLOW));
                            super.appendHoverText(stack, level, tooltip, flag);
                        }
                    });
    public static final RegistryObject<Item> DUSTCOLLECTOR =
            ITEMS.register("dustcollector",
                    () -> new BlockItem(
                            ModBlocks.DUSTCOLLECTOR.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> CRUDE_SULFUR_DIOXIDE_DUST =
            ITEMS.register("crude_sulfur_dioxide_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> WET_SULFUR_DIOXIDE_DUST =
            ITEMS.register("wet_sulfur_dioxide_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> PURIFIED_SULFUR_DIOXIDE_DUST =
            ITEMS.register("purified_sulfur_dioxide_dust",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> DUST_FROM_SULFUR_DIOXIDE =
            ITEMS.register("dust_from_sulfur_dioxide",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ARSENIC =
            ITEMS.register("arsenic",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> GYPSUM =
            ITEMS.register("gypsum",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SINTERINGFURNACE =
            ITEMS.register("sinteringfurnace",
                    () -> new BlockItem(
                            ModBlocks.SINTERINGFURNACE.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> IRON_CONCENTRATE =
            ITEMS.register("iron_concentrate",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SINTERED_IRON =
            ITEMS.register("sintered_iron",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> SINTERED_PIG_IRON =
            ITEMS.register("sintered_pig_iron",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> DESULFURIZED_SINTERED_PIG_IRON =
            ITEMS.register("desulfurized_sintered_pig_iron",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> VANADIUM_SLAG =
            ITEMS.register("vanadium_slag",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> VANADATE_ION =
            ITEMS.register("vanadate_ion",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> STRONGLY_BASIC_ION_EXCHANGE_RESIN_BEADS =
            ITEMS.register("strongly_basic_ion_exchanges_beads",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> ETHYLENE_PLANT =
            ITEMS.register("ethylene_plant",
                    () -> new BlockItem(
                            ModBlocks.ETHYLENE_PLANT.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> ETHYLENE_CAPSULE =
            ITEMS.register("ethylene_capsule",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> VANADIUM_PENTOXIDE =
            ITEMS.register("vanadium_pentoxide",
                    () -> new Item(new Item.Properties()
                    ));
    public static final RegistryObject<Item> POLYSTYRENE_PELLETS =
            ITEMS.register("polystyrene_pellets",
            () -> new Item(new Item.Properties()
            ));
    public static final RegistryObject<Item> ZEOLITE_CRYSTAL =
            ITEMS.register("zeolite_crystal",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZEOLITE_CATALYST =
            ITEMS.register("zeolite_catalyst",
            () -> new Item(new Item.Properties().durability(128))); // 128回反応に耐える仕様
    public static final RegistryObject<Item> OXIDIZER =
            ITEMS.register("oxidizer",
                    () -> new BlockItem(
                            ModBlocks.OXIDIZER.get(),
                            new Item.Properties()
                    ));
    public static final RegistryObject<Item> SULFUR_TRIOXIDE_DUST =
            ITEMS.register("sulfur_trioxide_dust",
                    () -> new Item(new Item.Properties()));
    public static final TagKey<Item> MOLD_TAG = ItemTags.create(new ResourceLocation("ultimatecraftv2", "mold"));

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

//後にアルミニウムインゴットのレシピでアルミナの洗浄に専用の水溶液を用いらせる予定