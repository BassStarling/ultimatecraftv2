package com.bassstarling.ultimatecraftv2.fluid;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, UltimateCraftV2.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, UltimateCraftV2.MOD_ID);

    // FluidTypeの登録（液体の物理的・視覚的特性）
    public static final RegistryObject<FluidType> TAR_FLUID_TYPE = FLUID_TYPES.register("tar",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.ultimatecraftv2.tar")
                    .density(2000)       // 水(1000)より重い
                    .viscosity(5000)     // 水(1000)より非常に粘り強い
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation TAR_STILL = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/tar_still");
                        private static final ResourceLocation TAR_FLOW = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/tar_flow");

                        @Override
                        public ResourceLocation getStillTexture() { return TAR_STILL; }
                        @Override
                        public ResourceLocation getFlowingTexture() { return TAR_FLOW; }

                        @Override
                        public int getTintColor() {
                            return 0xFF111111; // 非常に濃い黒
                        }
                    });
                }
            });
    public static final RegistryObject<FluidType> SODIUM_HYDROXIDE_FLUID_TYPE = FLUID_TYPES.register("sodium_hydroxide",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.ultimatecraftv2.sodium_hydroxide")
                    .density(2000)       // 水(1000)より重い
                    .viscosity(5000)     // 水(1000)より非常に粘り強い
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation SODIUM_HYDROXIDE_STILL = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_still");
                        private static final ResourceLocation SODIUM_HYDROXIDE_FLOW = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_flow");

                        @Override
                        public ResourceLocation getStillTexture() { return SODIUM_HYDROXIDE_STILL; }
                        @Override
                        public ResourceLocation getFlowingTexture() { return SODIUM_HYDROXIDE_FLOW; }

                        @Override
                        public int getTintColor() {
                            return 0xFFE0E0E0; // 明るいグレー（白濁した液体）
                        }
                    });
                }
            });
    public static final RegistryObject<FluidType> SODIUM_ALUMINATE_SUSPENSION_FLUID_TYPE = FLUID_TYPES.register("sodium_aluminate_suspension",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.ultimatecraftv2.sodium_aluminate_suspension")
                    .density(2000)       // 水(1000)より重い
                    .viscosity(5000)     // 水(1000)より非常に粘り強い
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation SODIUM_ALUMINATE_SUSPENSION_STILL = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_still");
                        private static final ResourceLocation SODIUM_ALUMINATE_SUSPENSION_FLOW = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_flow");

                        @Override
                        public ResourceLocation getStillTexture() { return SODIUM_ALUMINATE_SUSPENSION_STILL; }
                        @Override
                        public ResourceLocation getFlowingTexture() { return SODIUM_ALUMINATE_SUSPENSION_FLOW; }

                        @Override
                        public int getTintColor() {
                            return 0xFF6F3528; // 暗い赤褐色（汚れの混ざったスラリー）
                        }
                    });
                }
            });
    public static final RegistryObject<FluidType> SODIUM_ALUMINATE_FLUID_TYPE = FLUID_TYPES.register("sodium_aluminate",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.ultimatecraftv2.sodium_aluminate")
                    .density(2000)       // 水(1000)より重い
                    .viscosity(5000)     // 水(1000)より非常に粘り強い
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation SODIUM_ALUMINATE_STILL = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_still");
                        private static final ResourceLocation SODIUM_ALUMINATE_FLOW = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_flow");

                        @Override
                        public ResourceLocation getStillTexture() { return SODIUM_ALUMINATE_STILL; }
                        @Override
                        public ResourceLocation getFlowingTexture() { return SODIUM_ALUMINATE_FLOW; }

                        @Override
                        public int getTintColor() {
                            return 0xFFC88C32; // 澄んだ琥珀色（工業的な貴液のイメージ）
                        }
                    });
                }
            });
    public static final RegistryObject<FluidType> RED_MUD_FLUID_TYPE = FLUID_TYPES.register("red_mud",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.ultimatecraftv2.red_mud")
                    .density(2000)       // 水(1000)より重い
                    .viscosity(5000)     // 水(1000)より非常に粘り強い
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {

                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation RED_MUD_STILL = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_still");
                        private static final ResourceLocation RED_MUD_FLOW = new ResourceLocation(UltimateCraftV2.MOD_ID, "block/water_flow");

                        @Override
                        public ResourceLocation getStillTexture() { return RED_MUD_STILL; }
                        @Override
                        public ResourceLocation getFlowingTexture() { return RED_MUD_FLOW; }

                        @Override
                        public int getTintColor() {
                            return 0xFFA52A2A; // 鉄錆のような赤褐色
                        }
                    });
                }
            });

    // 本体の登録（ソースと流れる液体）
    public static final RegistryObject<ForgeFlowingFluid> SOURCE_TAR = FLUIDS.register("tar",
            () -> new ForgeFlowingFluid.Source(ModFluids.TAR_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> FLOWING_TAR = FLUIDS.register("flowing_tar",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.TAR_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> SOURCE_SODIUM_HYDROXIDE = FLUIDS.register("sodium_hydroxide",
            () -> new ForgeFlowingFluid.Source(ModFluids.SODIUM_HYDROXIDE_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> FLOWING_SODIUM_HYDROXIDE = FLUIDS.register("flowing_sodium_hydroxide",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.SODIUM_HYDROXIDE_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> SOURCE_SODIUM_ALUMINATE = FLUIDS.register("sodium_aluminate",
            () -> new ForgeFlowingFluid.Source(ModFluids.SODIUM_ALUMINATE_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> FLOWING_SODIUM_ALUMINATE = FLUIDS.register("flowing_sodium_aluminate",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.SODIUM_ALUMINATE_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> SOURCE_SODIUM_ALUMINATE_SUSPENSION = FLUIDS.register("sodium_aluminate_suspension",
            () -> new ForgeFlowingFluid.Source(ModFluids.SODIUM_ALUMINATE_SUSPENSION_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> FLOWING_SODIUM_ALUMINATE_SUSPENSION = FLUIDS.register("flowing_sodium_aluminate_suspension",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.SODIUM_ALUMINATE_SUSPENSION_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> SOURCE_RED_MUD = FLUIDS.register("red_mud",
            () -> new ForgeFlowingFluid.Source(ModFluids.RED_MUD_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> FLOWING_RED_MUD = FLUIDS.register("flowing_red_mud",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.RED_MUD_PROPERTIES));

    // プロパティの定義
    public static final ForgeFlowingFluid.Properties TAR_PROPERTIES = new ForgeFlowingFluid.Properties(
            TAR_FLUID_TYPE, SOURCE_TAR, FLOWING_TAR)
            .slopeFindDistance(2) // 粘り気があるのであまり遠くまで流れない
            .levelDecreasePerBlock(2)
            .bucket(ModItems.TAR_BUCKET);
    public static final ForgeFlowingFluid.Properties SODIUM_HYDROXIDE_PROPERTIES = new ForgeFlowingFluid.Properties(
            SODIUM_HYDROXIDE_FLUID_TYPE, SOURCE_SODIUM_HYDROXIDE, FLOWING_SODIUM_HYDROXIDE)
            .slopeFindDistance(2) // 粘り気があるのであまり遠くまで流れない
            .levelDecreasePerBlock(2)
            .bucket(ModItems.SODIUM_HYDROXIDE_BUCKET);
    public static final ForgeFlowingFluid.Properties SODIUM_ALUMINATE_PROPERTIES = new ForgeFlowingFluid.Properties(
            SODIUM_ALUMINATE_FLUID_TYPE, SOURCE_SODIUM_ALUMINATE, FLOWING_SODIUM_ALUMINATE)
            .slopeFindDistance(2) // 粘り気があるのであまり遠くまで流れない
            .levelDecreasePerBlock(2)
            .bucket(ModItems.SODIUM_ALUMINATE_BUCKET);
    public static final ForgeFlowingFluid.Properties SODIUM_ALUMINATE_SUSPENSION_PROPERTIES = new ForgeFlowingFluid.Properties(
            SODIUM_ALUMINATE_SUSPENSION_FLUID_TYPE, SOURCE_SODIUM_ALUMINATE_SUSPENSION, FLOWING_SODIUM_ALUMINATE_SUSPENSION)
            .slopeFindDistance(2) // 粘り気があるのであまり遠くまで流れない
            .levelDecreasePerBlock(2)
            .bucket(ModItems.SODIUM_ALUMINATE_SUSPENSION_BUCKET);
    public static final ForgeFlowingFluid.Properties RED_MUD_PROPERTIES = new ForgeFlowingFluid.Properties(
            RED_MUD_FLUID_TYPE, SOURCE_RED_MUD, FLOWING_RED_MUD)
            .slopeFindDistance(2) // 粘り気があるのであまり遠くまで流れない
            .levelDecreasePerBlock(2)
            .bucket(ModItems.RED_MUD_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}