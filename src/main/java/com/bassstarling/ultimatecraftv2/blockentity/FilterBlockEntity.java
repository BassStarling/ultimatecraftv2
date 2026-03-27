package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.menu.FilterMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
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
import net.minecraftforge.items.ItemStackHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class FilterBlockEntity extends BlockEntity implements MenuProvider {
    // 1. 入力タンク (懸濁液: sodium_aluminate_suspension)
    private final FluidTank inputTank = new FluidTank(10000);

    // 2. 出力タンクA (赤泥: red_mud) - 座標 (84, 15) に対応
    private final FluidTank redMudTank = new FluidTank(10000);

    // 3. 出力タンクB (純液: sodium_aluminate) - 座標 (126, 15) に対応
    private final FluidTank pureAluminateTank = new FluidTank(10000);

    // 以前の設計通り、不純物（赤い泥）をアイテムでも取り出す場合のスロット
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public FilterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FILTERBE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FilterBlockEntity.this.progress;
                    case 1 -> FilterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FilterBlockEntity.this.progress = value;
                    case 1 -> FilterBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (canFilter()) {
            progress++;
            if (progress >= maxProgress) {
                doFilter();
                progress = 0;
            }
        } else {
            progress = 0;
        }

        // 液体搬出処理を呼び出し
        exportFluids();

        setChanged();
    }

    private void exportFluids() {
        for (Direction direction : Direction.values()) {
            // 上面(UP)は入力専用なので、こちらから押し出す処理(export)からは除外する
            if (direction == Direction.UP) continue;

            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null) continue;

            neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                // 下面なら赤泥、横面なら純液をターゲットにする
                FluidTank targetTank = (direction == Direction.DOWN) ? redMudTank : pureAluminateTank;

                if (!targetTank.isEmpty()) {
                    FluidStack toDrain = targetTank.drain(250, IFluidHandler.FluidAction.SIMULATE);
                    int filled = handler.fill(toDrain, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        targetTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        setChanged();
                    }
                }
            });
        }
    }

    private boolean canFilter() {
        // 入力があり、中身が「懸濁液」であり、両方の出力タンクに空きがあるか確認
        return !inputTank.isEmpty() &&
                // getFluid() で FluidStack を取得し、その Fluid が一致するか判定
                inputTank.getFluid().getFluid().isSame(ModFluids.SOURCE_SODIUM_ALUMINATE_SUSPENSION.get()) &&
                redMudTank.getFluidAmount() <= 9000 &&
                pureAluminateTank.getFluidAmount() <= 9000;
    }

    private void doFilter() {
        // 入力 1000mB を消費
        inputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);

        // 赤泥 (300mB) と 純液 (700mB) に分離
        redMudTank.fill(new FluidStack(ModFluids.SOURCE_RED_MUD.get(), 300), IFluidHandler.FluidAction.EXECUTE);
        pureAluminateTank.fill(new FluidStack(ModFluids.SOURCE_SODIUM_ALUMINATE.get(), 700), IFluidHandler.FluidAction.EXECUTE);

        // 任意: 物理的な「泥」のアイテムも少量排出する場合
        if (level.random.nextFloat() < 0.1f) {
            itemHandler.insertItem(0, new ItemStack(ModItems.RED_MUD_CHUNK.get()), false);
        }
    }

    // --- GUI & Network ---

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ultimatecraftv2.filter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FilterMenu(containerId, playerInventory, this, this.data);
    }

    // NBT保存・読み込み (省略していますが、3つのタンク分すべて必要です)
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inputTank", inputTank.writeToNBT(new CompoundTag()));
        nbt.put("redMudTank", redMudTank.writeToNBT(new CompoundTag()));
        nbt.put("pureAluminateTank", pureAluminateTank.writeToNBT(new CompoundTag()));
        nbt.putInt("progress", progress);
        super.saveAdditional(nbt);
    }

    // Getters for Menu/Screen
    public FluidTank getInputTank() { return inputTank; }
    public FluidTank getRedMudTank() { return redMudTank; }
    public FluidTank getPureAluminateTank() { return pureAluminateTank; }

    // タンクごとに LazyOptional を用意（onLoadで初期化、invalidateCapsで無効化が必要）
    private final LazyOptional<IFluidHandler> inputCap = LazyOptional.of(() -> inputTank);
    private final LazyOptional<IFluidHandler> redMudCap = LazyOptional.of(() -> redMudTank);
    private final LazyOptional<IFluidHandler> pureAluminateCap = LazyOptional.of(() -> pureAluminateTank);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 流体ハンドラーの要求が来た場合
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            // 1. 方向指定なし（内部処理など）は入力タンクを返す
            if (side == null) return inputCap.cast();

            // 2. 上面 (UP)：浸出器(Digester)からの「懸濁液」を受け入れる
            if (side == Direction.UP) {
                return inputCap.cast();
            }

            // 3. 下面 (DOWN)：分解層や廃棄用へ「赤泥」を出力する
            if (side == Direction.DOWN) {
                return redMudCap.cast();
            }

            // 4. 横面 (NORTH, SOUTH, EAST, WEST)：分解層へ「純液」を出力する
            // これにより、前後左右に隣接した分解層へ自動供給されます
            return pureAluminateCap.cast();
        }

        // アイテム（赤泥の塊など）の搬出用
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> itemHandler).cast();
        }

        return super.getCapability(cap, side);
    }
}