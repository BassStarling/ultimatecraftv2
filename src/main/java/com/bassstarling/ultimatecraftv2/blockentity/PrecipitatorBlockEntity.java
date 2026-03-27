package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.menu.PrecipitatorMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class PrecipitatorBlockEntity extends BlockEntity implements MenuProvider {
    // 1. 入力タンク (sodium_aluminate または red_mud)
    private final FluidTank inputTank = new FluidTank(10000);
    // 2. 出力タンク (回収用 sodium_hydroxide)
    private final FluidTank outputTank = new FluidTank(10000);

    // アイテムスロット: 0:出力スロット（水酸化アルミ粉 / 赤い泥の塊）
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200; // 結晶化プロセス用の時間

    public PrecipitatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRECIPITATOR_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> PrecipitatorBlockEntity.this.progress;
                    case 1 -> PrecipitatorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> PrecipitatorBlockEntity.this.progress = value;
                    case 1 -> PrecipitatorBlockEntity.this.maxProgress = value;
                }
            }
            @Override
            public int getCount() { return 2; }
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (canProcess()) {
            progress++;
            if (progress >= maxProgress) {
                doProcess();
                progress = 0;
            }
        } else {
            progress = 0;
        }
        setChanged();

        dropOutputItem();
    }

    private boolean canProcess() {
        if (inputTank.isEmpty() || inputTank.getFluidAmount() < 1000) return false;

        FluidStack inputStack = inputTank.getFluid();

        // パターンA: アルミ酸ナトリウム溶液 -> 水酸化アルミニウム粉 + 苛性ソーダ
        if (inputStack.getFluid().isSame(ModFluids.SOURCE_SODIUM_ALUMINATE.get())) {
            // 出力タンクに苛性ソーダが入る余裕があり、アイテムスロットに空きがあるか
            return outputTank.fill(new FluidStack(ModFluids.SOURCE_SODIUM_HYDROXIDE.get(), 800), IFluidHandler.FluidAction.SIMULATE) >= 800 &&
                    itemHandler.getStackInSlot(0).getCount() < 64;
        }

        // パターンB: 赤い泥（液体） -> 赤い泥の塊（アイテム）
        if (inputStack.getFluid().isSame(ModFluids.SOURCE_RED_MUD.get())) {
            // アイテムスロットに空きがあるか
            return itemHandler.getStackInSlot(0).getCount() < 64;
        }

        return false;
    }

    private void dropOutputItem() {
        // 1. 下のブロックが空気かどうかチェック
        BlockPos below = worldPosition.below();
        if (!level.getBlockState(below).isAir()) return;

        // 2. スロット0（生成されたアイテム）をチェック
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return;

        // 3. ドロップ処理 (1つずつポタポタ落とす)
        ItemStack toDrop = itemHandler.extractItem(0, 1, false);
        if (!toDrop.isEmpty()) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() - 0.1; // ブロックの底面ギリギリ
            double z = worldPosition.getZ() + 0.5;

            ItemEntity itemEntity = new ItemEntity(level, x, y, z, toDrop);

            // 真下にスッと落ちるように速度を設定
            itemEntity.setDeltaMovement(0, -0.05, 0);

            level.addFreshEntity(itemEntity);
            // ドロップしたことをマーク
            setChanged();
        }
    }

    private void doProcess() {
        FluidStack inputStack = inputTank.getFluid();

        // どちらのパターンでも入力液体 1000mB を消費
        inputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);

        if (inputStack.getFluid().isSame(ModFluids.SOURCE_SODIUM_ALUMINATE.get())) {
            // 水酸化アルミニウム粉を生成し、苛性ソーダを回収
            itemHandler.insertItem(0, new ItemStack(ModItems.WHITE_FLUFFY_SOLID_OF_ALUMINIUM_HYDROXIDE.get()), false);
            outputTank.fill(new FluidStack(ModFluids.SOURCE_SODIUM_HYDROXIDE.get(), 800), IFluidHandler.FluidAction.EXECUTE);
        }
        else if (inputStack.getFluid().isSame(ModFluids.SOURCE_RED_MUD.get())) {
            // 赤い泥の塊を生成
            itemHandler.insertItem(0, new ItemStack(ModItems.RED_MUD_CHUNK.get()), false);
        }
    }

    // --- インベントリドロップ (Block破壊時) ---
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    // --- GUI & Network 関連 ---
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ultimatecraftv2.precipitator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new PrecipitatorMenu(id, inv, this, this.data);
    }

    // NBT保存
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("InputTank", inputTank.writeToNBT(new CompoundTag()));
        nbt.put("OutputTank", outputTank.writeToNBT(new CompoundTag()));
        nbt.put("Inventory", itemHandler.serializeNBT());
        nbt.putInt("precipitator.progress", progress);
    }

    // NBT読み込み
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        inputTank.readFromNBT(nbt.getCompound("InputTank"));
        outputTank.readFromNBT(nbt.getCompound("OutputTank"));
        itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        progress = nbt.getInt("precipitator.progress");
    }

    // Capability
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyInputTank = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyOutputTank = LazyOptional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyInputTank = LazyOptional.of(() -> inputTank);
        lazyOutputTank = LazyOptional.of(() -> outputTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyInputTank.invalidate();
        lazyOutputTank.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == Direction.DOWN) return lazyOutputTank.cast(); // 下から苛性ソーダ搬出
            return lazyInputTank.cast(); // それ以外から材料搬入
        }
        return super.getCapability(cap, side);
    }

    // Getters for Screen
    public FluidTank getInputTank() { return inputTank; }
    public FluidTank getOutputTank() { return outputTank; }
}