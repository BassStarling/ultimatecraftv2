package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.menu.DustCollectorMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DustCollectorBlockEntity extends BlockEntity implements MenuProvider, Container {

    // 3スロット用意 (0:入力, 1:綺麗なガス, 2:粉塵)
    public final ItemStackHandler inventory = new ItemStackHandler(3);

    // 【追加】最大36,000 FEのエネルギー貯蔵（外部からの入力上限を1Tickあたり1,000FEに設定）
    public final EnergyStorage energyStorage = new EnergyStorage(36000, 1000, 1000);

    private final LazyOptional<ItemStackHandler> itemCapability = LazyOptional.of(() -> inventory);
    // 【追加】外部のエネルギーパイプや発電機と接続するためのEnergy Capability
    private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> energyStorage);

    private int progress = 0;
    private final int maxProgress = 160; // 2秒で1回加工

    // 【設定】1Tickあたりに消費するエネルギー量（バランスを見て調整してください）
    private final int energyUsagePerTick = 1;

    public DustCollectorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.DUST_COLLECTOR.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> DustCollectorBlockEntity.this.progress;
                    case 1 -> DustCollectorBlockEntity.this.energyStorage.getEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> DustCollectorBlockEntity.this.progress = value;
                    case 1 -> DustCollectorBlockEntity.this.energyStorage.extractEnergy(DustCollectorBlockEntity.this.energyStorage.getEnergyStored(), false); // 一度空にする
                }
                // ※EnergyStorageの直接の値を強制セットする簡易ロジック（必要に応じて調整）
                if (index == 1) {
                    DustCollectorBlockEntity.this.energyStorage.receiveEnergy(value, false);
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        }; // ⚠️ セミコロンを忘れずに！
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack input = inventory.getStackInSlot(0);

        // 1. レシピ（アイテム条件）の確認
        if (!input.isEmpty() && input.is(ModItems.SULFUR_DIOXIDE_DUST.get())) {

            // 【変更】アイテムの空きスペースに加えて、「必要なエネルギーが貯まっているか」もチェック
            if (canProcess() && energyStorage.getEnergyStored() >= energyUsagePerTick) {

                // 1Tick分の電力を消費
                energyStorage.extractEnergy(energyUsagePerTick, false);

                this.progress++;
                if (this.progress >= maxProgress) {
                    completeProcess();
                }
                setChanged();
            } else {
                // 電力不足、またはスペース不足なら進行度を維持、またはリセット（今回は維持）
                if (this.progress > 0 && energyStorage.getEnergyStored() < energyUsagePerTick) {
                    // 電力が足りなくて止まっている場合は、進行度を少しずつ減衰させてもリアルです
                    this.progress = Math.max(0, this.progress - 1);
                    setChanged();
                }
            }
        } else {
            this.progress = 0;
        }

        autoPushOutput();
    }

    private boolean canProcess() {
        ItemStack cleanGasResult = new ItemStack(ModItems.CRUDE_SULFUR_DIOXIDE_DUST.get());
        ItemStack dustResult = new ItemStack(ModItems.DUST_FROM_SULFUR_DIOXIDE.get());

        ItemStack remainderGas = inventory.insertItem(1, cleanGasResult, true);
        ItemStack remainderDust = inventory.insertItem(2, dustResult, true);

        return remainderGas.isEmpty() && remainderDust.isEmpty();
    }

    private void completeProcess() {
        // 二酸化硫黄を消費
        inventory.getStackInSlot(0).shrink(1);

        // スロット1に「粗製二酸化硫黄」を出力
        inventory.insertItem(1, new ItemStack(ModItems.CRUDE_SULFUR_DIOXIDE_DUST.get()), false);

        // スロット2に 70% の確率で「二酸化硫黄からの粉塵」が溜まる
        if (level.random.nextFloat() < 0.7f) {
            inventory.insertItem(2, new ItemStack(ModItems.DUST_FROM_SULFUR_DIOXIDE.get()), false);
        }

        this.progress = 0;
    }

    private void autoPushOutput() {
        BlockEntity belowBe = level.getBlockEntity(worldPosition.below());
        if (belowBe != null) {
            belowBe.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(handler -> {
                for (int i = 1; i <= 2; i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        ItemStack toPush = stack.copy();
                        toPush.setCount(1);
                        ItemStack left = handler.insertItem(0, toPush, false);
                        if (left.isEmpty()) {
                            stack.shrink(1);
                            setChanged();
                            break;
                        }
                    }
                }
            });
        }
    }

    // --- NBTセーブ＆ロード（エネルギーの保存を追加） ---
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("Inventory", inventory.serializeNBT());
        pTag.putInt("Energy", energyStorage.getEnergyStored()); // 【追加】エネルギー残量の保存
        pTag.putInt("Progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inventory.deserializeNBT(pTag.getCompound("Inventory"));
        // 【追加】エネルギー残量の復元（EnergyStorageは内部値を受け取るメソッドがないため、新しくインスタンスを生成するか、独自にラップする必要がありますが、一番シンプルな形で復元します）
        int storedEnergy = pTag.getInt("Energy");
        // 蓄電器の中身を一度空にして、保存されていた分だけ注入する
        energyStorage.extractEnergy(energyStorage.getMaxEnergyStored(), false);
        energyStorage.receiveEnergy(storedEnergy, false);

        progress = pTag.getInt("Progress");
    }

    // クラス内変数に追加
    protected final ContainerData data;

    // createMenu
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new DustCollectorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    // --- Capabilities の拡張 ---
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 【追加】外部の電力パイプ（メカニズムのケーブルなど）が接続できるようにする
        if (cap == ForgeCapabilities.ENERGY) return energyCapability.cast();
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast();
        return super.getCapability(cap, side);
    }

    // --- Container 実装（省略せずそのまま残してください） ---
    @Override public int getContainerSize() { return inventory.getSlots(); }
    @Override public boolean isEmpty() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }
    @Override public ItemStack getItem(int index) { return inventory.getStackInSlot(index); }
    @Override public ItemStack removeItem(int index, int count) {
        ItemStack result = inventory.extractItem(index, count, false);
        if (!result.isEmpty()) setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = inventory.getStackInSlot(index);
        inventory.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }
    @Override public void setItem(int index, ItemStack stack) { inventory.setStackInSlot(index, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }
    @Override public Component getDisplayName() { return Component.translatable(this.getBlockState().getBlock().getDescriptionId()); }
}