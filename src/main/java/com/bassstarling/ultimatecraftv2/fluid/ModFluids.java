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

    // 1. FluidTypeの登録（液体の物理的・視覚的特性）
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

    // 2. 本体の登録（ソースと流れる液体）
    public static final RegistryObject<ForgeFlowingFluid> SOURCE_TAR = FLUIDS.register("tar",
            () -> new ForgeFlowingFluid.Source(ModFluids.TAR_PROPERTIES));
    public static final RegistryObject<ForgeFlowingFluid> FLOWING_TAR = FLUIDS.register("flowing_tar",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.TAR_PROPERTIES));

    // 3. プロパティの定義
    public static final ForgeFlowingFluid.Properties TAR_PROPERTIES = new ForgeFlowingFluid.Properties(
            TAR_FLUID_TYPE, SOURCE_TAR, FLOWING_TAR)
            .slopeFindDistance(2) // 粘り気があるのであまり遠くまで流れない
            .levelDecreasePerBlock(2)
            .bucket(ModItems.TAR_BUCKET); // 下記で作成するアイテム

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
