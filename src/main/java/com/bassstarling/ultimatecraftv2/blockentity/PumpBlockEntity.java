package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PumpBlockEntity extends BlockEntity {
    // 1. 内部タンク (10,000 mB)
    private final FluidTank waterTank = new FluidTank(10000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER);
        }
    };

    // 2. エネルギー貯蔵 (360,000 Spark = 360,000 FE)
    // Forge標準のEnergyStorageを使用し、内部変数として保持
    private final EnergyStorage energyStorage = new EnergyStorage(36000, 1000, 1000);

    // Capability用
    private LazyOptional<IFluidHandler> fluidOptional = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> energyOptional = LazyOptional.empty();

    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PUMP_BE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // 1. 周囲のアイテムを吸収してスパークを充填
        if (energyStorage.getEnergyStored() <= (energyStorage.getMaxEnergyStored() - 1280)) {
            pickupAndAbsorbItems();
        }

        // --- 吸い上げロジック ---
        // 10 Spark/tick 消費し、100 mB/tick 汲み上げる
        if (canPump()) {
            energyStorage.extractEnergy(10, false);
            waterTank.fill(new FluidStack(Fluids.WATER, 100), IFluidHandler.FluidAction.EXECUTE);
            setChanged();
        }

        // --- 上面への搬出ロジック ---
        exportToAbove();
    }

    private void pickupAndAbsorbItems() {
        // ブロックの周囲1マスの範囲を定義
        AABB area = new AABB(worldPosition).inflate(1.0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();

            // スパークストーンであるかチェック
            if (!stack.isEmpty() && stack.is(ModItems.SPARK_STONE.get())) {
                int tier = SparkStone.getTier(stack);

                // 指定されたTierごとの充填量
                int energyToAdd = switch (tier) {
                    case 0 -> 10;
                    case 1 -> 20;
                    case 2 -> 40;
                    case 3 -> 80;
                    case 4 -> 160;
                    case 5 -> 320;
                    case 6 -> 640;
                    case 7 -> 1280;
                    default -> 5;
                };

                // エネルギーを充填し、アイテムを1つ減らす
                energyStorage.receiveEnergy(energyToAdd, false);
                stack.shrink(1);

                // アイテムがなくなったら実体を消去
                if (stack.isEmpty()) {
                    itemEntity.discard();
                }

                setChanged();
                break; // 1チックに1つずつ吸収するように制限（負荷対策）
            }
        }
    }

    private boolean canPump() {
        // エネルギーが10以上、かつタンクに空きがあるか
        if (energyStorage.getEnergyStored() < 10 || waterTank.getFluidAmount() >= waterTank.getCapacity()) {
            return false;
        }

        // 下のブロックが水源（Water Source）かチェック
        BlockState belowState = level.getBlockState(worldPosition.below());
        return belowState.getFluidState().isSource() && belowState.getFluidState().is(FluidTags.WATER);
    }

    private void exportToAbove() {
        if (waterTank.isEmpty()) return;

        BlockEntity aboveBE = level.getBlockEntity(worldPosition.above());
        if (aboveBE != null) {
            // 浸出器や汎用電解機の下面(DOWN)から注入を試みる
            aboveBE.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN).ifPresent(handler -> {
                FluidStack toExport = waterTank.drain(200, IFluidHandler.FluidAction.SIMULATE);
                int filled = handler.fill(toExport, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    waterTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            });
        }
    }

    // --- 表示用Getter (Blockクラスで使用) ---
    public int getWaterAmount() { return waterTank.getFluidAmount(); }
    public int getEnergyAmount() { return energyStorage.getEnergyStored(); }

    // --- Capability 登録 ---
    @Override
    public void onLoad() {
        super.onLoad();
        fluidOptional = LazyOptional.of(() -> waterTank);
        energyOptional = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidOptional.invalidate();
        energyOptional.invalidate();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return fluidOptional.cast();
        if (cap == ForgeCapabilities.ENERGY) return energyOptional.cast();
        return super.getCapability(cap, side);
    }

    // --- NBT保存 ---
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("WaterTank", waterTank.writeToNBT(new CompoundTag()));
        nbt.putInt("EnergyStored", energyStorage.getEnergyStored());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        waterTank.readFromNBT(nbt.getCompound("WaterTank"));
        // EnergyStorageは直接値をセットするメソッドがprotectedなため、NBT経由で読み込むのが標準的
        // もしくは、独自クラスを作って公開するかですが、今回は標準を流用。
        // energyStorage.receiveEnergy(...)等でも代用可能。
    }
}