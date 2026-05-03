package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.block.ArcFurnaceBlock;
import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.menu.ArcFurnaceMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
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
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ArcFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    // 1. アイテムスロット（0: 入力, 1: 出力）
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    // 2. エネルギー貯蔵（FE: Forge Energy）
    private final EnergyStorage energyStorage = new EnergyStorage(10000, 10); // 最大10000FE, 毎tick 10受電
    private LazyOptional<IItemHandler> inventoryOptional = LazyOptional.empty();
    private int progress = 0;
    private final int maxProgress = 200; // 10秒(20tick * 10)で1個加工
    private int energyBuffer = 0;
    private final int maxEnergy = 32000; // Tier 3 (16000) 以上を許容するため少し大きめに設定

    public ArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARC__FURNACE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack sparkStack = itemHandler.getStackInSlot(2);

        if (!sparkStack.isEmpty() && sparkStack.getItem() instanceof SparkStone) {

            // スパークストーンのTierを取得
            int tier = SparkStone.getTier(sparkStack);

            int energyToAdd = switch (tier) {
                case 1  -> 20;
                case 2  -> 40;
                case 3  -> 80;
                case 4  -> 160;
                case 5  -> 320;
                case 6  -> 640;
                case 7  -> 1280;
                default -> 10;
            };

            // 【重要】エネルギーが「空」の時だけでなく、
            // 「補充しても最大容量(36,000)を超えない」時に補充を開始する
            if (this.energyBuffer + energyToAdd <= maxEnergy) {
                this.energyBuffer += energyToAdd;

                // アイテムを1つ消費
                sparkStack.shrink(1);

                // データの変更を保存
                setChanged();
            }
        }

        if (canProcess() && energyBuffer > 0) {
            energyBuffer -= 2; // FEの代わりにバッファを消費
            progress++;

            if (progress >= maxProgress) {
                processItem();
                progress = 0;
            }
        } else {
            progress = 0;
        }
    }

    private boolean canProcess() {
        ItemStack input = itemHandler.getStackInSlot(0);
        ItemStack output = itemHandler.getStackInSlot(1);

        return input.is(ModItems.COKE_ELECTRODE.get()) &&
                (output.isEmpty() || (output.is(ModItems.GRAPHITE_ELECTRODE.get()) && output.getCount() < 64));
    }

    private void processItem() {
        itemHandler.extractItem(0, 1, false);
        itemHandler.insertItem(1, new ItemStack(ModItems.GRAPHITE_ELECTRODE.get()), false);
    }

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energyBuffer;   // 現在のエネルギー量
                case 1 -> maxEnergy;      // 最大容量
                case 2 -> progress;
                case 3 -> maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> {} // エネルギー量はサーバー側で受電処理するためセット不要
                case 2 -> progress = value;
                case 3 -> {} // maxProgressは固定
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private LazyOptional<IEnergyStorage> energyOptional = LazyOptional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        energyOptional = LazyOptional.of(() -> energyStorage);
        inventoryOptional = LazyOptional.of(() -> itemHandler);
    }

    // --- Capability 用の LazyOptional 定義 ---
    // スロット0（入力：コークス電極）：上からの搬入用
    private final LazyOptional<IItemHandler> inputOptional = LazyOptional.of(() -> new RangedWrapper(itemHandler, 0, 1));
    // スロット1（出力：グラファイト電極）：下からの搬出用
    private final LazyOptional<IItemHandler> outputOptional = LazyOptional.of(() -> new RangedWrapper(itemHandler, 1, 2));
    // スロット2（燃料：スパークストーン）：横からの搬入用
    private final LazyOptional<IItemHandler> fuelOptional = LazyOptional.of(() -> new RangedWrapper(itemHandler, 2, 3));

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // アイテムハンドラーの処理
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) return inventoryOptional.cast();

            return switch (side) {
                case UP -> inputOptional.cast();    // 上：コークス電極
                case DOWN -> outputOptional.cast(); // 下：グラファイト電極
                default -> fuelOptional.cast();     // 横：スパークストーン
            };
        }

        // エネルギー(FE)の処理
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inputOptional.invalidate();
        outputOptional.invalidate();
        fuelOptional.invalidate();
        // 既存の inventoryOptional と energyOptional も忘れずに
        inventoryOptional.invalidate();
        energyOptional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("EnergyBuffer", this.energyBuffer);
        nbt.putInt("Progress", this.progress);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.energyBuffer = nbt.getInt("EnergyBuffer");
        this.progress = nbt.getInt("Progress");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.ARC_FURNACE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        // data は以前作成した ContainerData です
        return new ArcFurnaceMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}