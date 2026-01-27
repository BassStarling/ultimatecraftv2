package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.block.ArcFurnaceBlock;
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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ArcFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    // 1. アイテムスロット（0: 入力, 1: 出力）
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    // 2. エネルギー貯蔵（FE: Forge Energy）
    private final EnergyStorage energyStorage = new EnergyStorage(10000, 500); // 最大10000FE, 毎tick 500受電
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

        // ここでスロット2（電力スロット）からアイテムを取得し、sparkStack と名付けます
        ItemStack sparkStack = itemHandler.getStackInSlot(2);

        // バッファが空で、かつアイテムがスパークストーンの場合
        if (energyBuffer <= 0 && !sparkStack.isEmpty() && sparkStack.is(ModItems.SPARK_STONE.get())) {

            // NBTタグ（Tier）を読み取る
            CompoundTag tag = sparkStack.getTag();
            int tier = (tag != null && tag.contains("Tier")) ? tag.getInt("Tier") : 0;

            // 指定したエネルギー量を代入
            int energyToAdd = switch (tier) {
                case 1  -> 1000;
                case 2  -> 4000;
                case 3  -> 16000;
                case 4  -> 32000; // 必要に応じて追加
                default -> 250;   // Tier 0 または NBTなし
            };

            this.energyBuffer = energyToAdd;

            // アイテムを1個減らす
            sparkStack.shrink(1);
            setChanged();
        }

        // 2. 加工ロジック (energyStorage ではなく energyBuffer を参照)
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

        // 3. 点灯(LIT)状態の更新
        boolean isProcessing = progress > 0; // 進捗が進んでいればON
        if (level.getBlockState(worldPosition).getValue(ArcFurnaceBlock.LIT) != isProcessing) {
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(ArcFurnaceBlock.LIT, isProcessing), 3);
        }
    }

    private boolean canProcess() {
        ItemStack input = itemHandler.getStackInSlot(0);
        ItemStack output = itemHandler.getStackInSlot(1);

        // ベーク済みカーボン電極が入っているかチェック
        return input.is(ModItems.BAKED_CARBON_ELECTRODE.get()) &&
                (output.isEmpty() || (output.is(ModItems.GRAPHITE_ELECTRODE.get()) && output.getCount() < 64));
    }

    private void processItem() {
        itemHandler.extractItem(0, 1, false);
        itemHandler.insertItem(1, new ItemStack(ModItems.GRAPHITE_ELECTRODE.get()), false);

        // アーク放電の音を鳴らす
//        level.playSound(null, worldPosition, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.5f, 2.0f);
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

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
        inventoryOptional.invalidate();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast(); // アイテムスロットを返す
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast(); // エネルギー(FE)を返す
        }
        return super.getCapability(cap, side);
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
