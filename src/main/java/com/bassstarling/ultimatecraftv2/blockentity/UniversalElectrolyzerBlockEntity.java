package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.block.UniversalElectrolyzerBlock;
import com.bassstarling.ultimatecraftv2.menu.UniversalElectrolyzerMenu;
import com.bassstarling.ultimatecraftv2.recipe.ModRecipes;
import com.bassstarling.ultimatecraftv2.recipe.UniversalElectrolyzerRecipe;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UniversalElectrolyzerBlockEntity extends BlockEntity implements MenuProvider {
    // タンク定義 (入力・出力)
    private final FluidTank inputTank = new FluidTank(10000);
    private final FluidTank outputTank = new FluidTank(10000);

    // アイテムハンドラー (0:入力, 1:出力)
    // ※電極と電力スロットは不要になったため2スロット構成
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0; // スロット0のみ搬入可能
        }
    };

    // エネルギー貯蔵 (外部受電対応)
    private final ModEnergyStorage energyStorage = new ModEnergyStorage(64000, 1000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int rc = super.receiveEnergy(maxReceive, simulate);
            if (rc > 0 && !simulate) setChanged();
            return rc;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int rc = super.extractEnergy(maxExtract, simulate);
            if (rc > 0 && !simulate) setChanged();
            return rc;
        }
    };

    // プログレス・定数
    private int progress = 0;
    private int maxProgress = 200;
    private static final int ENERGY_USAGE_PER_TICK = 20; // 電解1tickあたりの消費電力

    // LazyOptional (Capability用)
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyInputTank = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyOutputTank = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    public UniversalElectrolyzerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UNIVERSAL_ELECTROLYZER_BE.get(), pos, state);
    }

    // Tick処理 (メインロジック)
    public void tick() {
        if (level == null || level.isClientSide) return;

        if (canProcess()) {
            // エネルギーを消費
            energyStorage.extractEnergy(ENERGY_USAGE_PER_TICK, false);
            progress++;

            if (progress >= maxProgress) {
                doProcess();
                progress = 0;
            }
        } else {
            // 条件を満たさない場合は進行度を戻す、あるいは維持（今回はリセット）
            progress = 0;
        }

        // データの同期が必要なタイミングで呼び出す
        if (progress % 10 == 0) {
            setChanged();
        }

        if (!outputTank.isEmpty()) {
            pushFluids();
        }
    }

    // レシピの判定
    private boolean canProcess() {
        Optional<UniversalElectrolyzerRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        UniversalElectrolyzerRecipe r = recipe.get();

        // 1. エネルギーチェック
        if (energyStorage.getEnergyStored() < r.getEnergyUsage()) return false;

        // 2. 出力先の空きチェック (液体)
        // fillの結果(int)が、レシピの出力量(int)と一致するかを確認します
        int filledAmount = outputTank.fill(r.getOutputFluid(), IFluidHandler.FluidAction.SIMULATE);
        if (filledAmount < r.getOutputFluid().getAmount()) {
            return false; // 全量は入りきらない（満タンに近い）
        }

        // 3. 出力先の空きチェック (アイテム)
        ItemStack result = r.getOutputItem();
        if (!result.isEmpty()) {
            ItemStack currentOutput = itemHandler.getStackInSlot(1);
            if (!currentOutput.isEmpty()) {
                // アイテムが違う、またはスタック制限を超える場合はNG
                if (!currentOutput.is(result.getItem()) ||
                        currentOutput.getCount() + result.getCount() > result.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    private void pushFluids() {
        // 汎用電解機の向き（Facing）を取得して、その「正面」にあるブロックを確認
        // もし背面や側面に出したい場合は Direction を調整してください
        Direction facing = getBlockState().getValue(UniversalElectrolyzerBlock.FACING);
        BlockPos targetPos = worldPosition.relative(facing);
        BlockEntity targetBE = level.getBlockEntity(targetPos);

        if (targetBE != null) {
            // 隣のブロックが流体を受け入れられるか（Capability）を確認
            targetBE.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite()).ifPresent(handler -> {
                // 出力タンクから液体を取り出し、相手に流し込む（最大1000mB/tick）
                FluidStack stack = outputTank.getFluid().copy();
                stack.setAmount(Math.min(stack.getAmount(), 1000)); // 搬出速度制限

                int filled = handler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    outputTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            });
        }
    }

    // --- レシピの実行 ---
    private void doProcess() {
        getCurrentRecipe().ifPresent(recipe -> {
            // 材料の消費 (液体)
            inputTank.drain(recipe.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

            // 材料の消費 (アイテム: スロット0)
            itemHandler.extractItem(0, 1, false);

            // 成果物の生成 (液体)
            outputTank.fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);

            // 成果物の生成 (アイテム: スロット1)
            ItemStack result = recipe.getOutputItem();
            if (!result.isEmpty()) {
                itemHandler.insertItem(1, result, false);
            }

            // エネルギーの最終消費 (もしtickごとではなく完了時に引く場合)
            // energyStorage.extractEnergy(recipe.getEnergyUsage(), false);

            setChanged();
        });
    }

    // --- 現在の状況に合うレシピを探すヘルパー ---
    private Optional<UniversalElectrolyzerRecipe> getCurrentRecipe() {
        SimpleContainer inv = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.UNIVERSALELECTROLYZER_TYPE.get(), inv, level)
                .filter(r -> r.matches(inputTank.getFluid(), itemHandler.getStackInSlot(0)));
    }

    public class ModEnergyStorage extends EnergyStorage {
        public ModEnergyStorage(int capacity, int maxReceive) {
            super(capacity, maxReceive, 0);
        }

        // これで load メソッドのエラーが消えます
        public void setEnergy(int energy) {
            this.energy = energy;
        }
    }

    public FluidStack getInputFluid() {
        return inputTank.getFluid();
    }

    public FluidStack getOutputFluid() {
        return outputTank.getFluid();
    }

    // GUIとの同期データ (ContainerData)
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energyStorage.getEnergyStored();
                case 1 -> energyStorage.getMaxEnergyStored();
                case 2 -> progress;
                case 3 -> maxProgress;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            // クライアント側での同期用
            switch (index) {
                case 2 -> progress = value;
            }
        }
        @Override
        public int getCount() { return 4; }
    };

    // --- 8. データの保存・読み込み ---
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Progress", progress);
        nbt.put("InputTank", inputTank.writeToNBT(new CompoundTag()));
        nbt.put("OutputTank", outputTank.writeToNBT(new CompoundTag()));
        nbt.put("Inventory", itemHandler.serializeNBT());
        nbt.putInt("Energy", energyStorage.getEnergyStored());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt("Progress");
        inputTank.readFromNBT(nbt.getCompound("InputTank"));
        outputTank.readFromNBT(nbt.getCompound("OutputTank"));
        itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        energyStorage.setEnergy(nbt.getInt("Energy"));
    }

    // Capability 実装
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyInputTank = LazyOptional.of(() -> inputTank);
        lazyOutputTank = LazyOptional.of(() -> outputTank);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyInputTank.invalidate();
        lazyOutputTank.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return lazyEnergyHandler.cast();
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            // 簡易的に上面から入力、それ以外から出力
            return side == Direction.UP ? lazyInputTank.cast() : lazyOutputTank.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ultimatecraftv2.universal_electrolyzer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new UniversalElectrolyzerMenu(id, inv, this, this.data);
    }

    public IItemHandler getItemHandlerForDrop() { return itemHandler; }
}