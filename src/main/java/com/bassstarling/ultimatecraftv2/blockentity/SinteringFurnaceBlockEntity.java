package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.menu.SinteringFurnaceMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SinteringFurnaceBlockEntity extends BlockEntity implements MenuProvider, Container {

    // 5スロット構成 (0:鉄精鉱, 1:石灰岩, 2:コーク, 3:焼結鉱)
    public final ItemStackHandler inventory = new ItemStackHandler(4);
    // 最大36,000 FEのエネルギー貯蔵
    public final EnergyStorage energyStorage = new EnergyStorage(36000, 12800, 12800);

    private final LazyOptional<ItemStackHandler> itemCapability = LazyOptional.of(() -> inventory);
    private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> energyStorage);

    protected final ContainerData data;

    // 各種タイマー・進捗度
    private int progress = 0;
    private final int maxProgress = 200;    // 10秒で1回焼結 (200tick)
    private int burnTime = 0;               // コークスが燃えている残り時間(tick)
    private int maxBurnTime = 0;            // コークス1個の総燃焼時間

    private final int energyUsagePerTick = 7; // 毎Tick 7 FE消費

    public SinteringFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SINTERING_FURNACE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SinteringFurnaceBlockEntity.this.progress;
                    case 1 -> SinteringFurnaceBlockEntity.this.energyStorage.getEnergyStored();
                    case 2 -> SinteringFurnaceBlockEntity.this.burnTime;
                    case 3 -> SinteringFurnaceBlockEntity.this.maxBurnTime;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SinteringFurnaceBlockEntity.this.progress = value;
                    case 1 -> SinteringFurnaceBlockEntity.this.energyStorage.extractEnergy(SinteringFurnaceBlockEntity.this.energyStorage.getEnergyStored(), false);
                    case 2 -> SinteringFurnaceBlockEntity.this.burnTime = value;
                    case 3 -> SinteringFurnaceBlockEntity.this.maxBurnTime = value;
                }
                if (index == 1) SinteringFurnaceBlockEntity.this.energyStorage.receiveEnergy(value, false);
            }
            @Override
            public int getCount() {
                return 4; }
        };
    }

    public void tick() {
        if (level == null || level.isClientSide)
            return;

        boolean isBurning = this.burnTime > 0;
        if (isBurning) {
            this.burnTime--;
        }

        if (level.getGameTime() % 20 == 0) { // 1秒に1回
            suctionSparkStones();
        }

        // 動作条件の確認（材料・スロット空き・FE残量）
        if (hasMaterialsAndSpace() && energyStorage.getEnergyStored() >= energyUsagePerTick) {

            // 燃焼が切れており、かつコークがスロット2にあれば消費して燃料補給
            if (!isBurning && hasCokeFuel()) {
                consumeCoke();
                isBurning = true;
            }

            // 炉が燃えており、かつ電力があれば進捗ゲージを進める
            if (isBurning) {
                energyStorage.extractEnergy(energyUsagePerTick, false);
                this.progress++;
                if (this.progress >= maxProgress) {
                    completeSintering();
                }
                setChanged();
            } else {
                stopProgress();
            }
        } else {
            stopProgress();
        }
    }

    private void suctionSparkStones() {
        // 範囲内のアイテムエンティティを探索
        AABB area = new AABB(worldPosition).inflate(2.0D);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity entity : items) {
            ItemStack stack = entity.getItem();
            if (stack.is(ModItems.SPARK_STONE.get())) {
                int tier = SparkStone.getTier(stack);

                // Tierに応じたエネルギー回復量
                int energyToAdd = switch (tier) {
                    case 1 -> 200;
                    case 2 -> 400;
                    case 3 -> 800;
                    case 4 -> 1600;
                    case 5 -> 3200;
                    case 6 -> 6400;
                    case 7 -> 12800;
                    default -> 100;
                };

                // FEを受け入れられるか確認
                int received = energyStorage.receiveEnergy(energyToAdd, true);
                if (received >= 100) { // 最低でも100FE分以上空きがあれば吸収
                    energyStorage.receiveEnergy(energyToAdd, false);
                    stack.shrink(1);
                    if (stack.isEmpty()) entity.discard();
                    setChanged();
                }
            }
        }
    }

    private boolean hasMaterialsAndSpace() {
        ItemStack ore = inventory.getStackInSlot(0);
        ItemStack limestone = inventory.getStackInSlot(1);

        // スロットが空なら即座に動かさない
        if (ore.isEmpty() || limestone.isEmpty())
            return false;

        // スロット0に鉄精鉱、スロット1に石灰岩が入っているか厳密にチェック
        if (!ore.is(ModItems.IRON_CONCENTRATE.get()) || !limestone.is(ModItems.LIMESTONE.get()))
            return false;

        // 出力スロット(3:焼結鉱)
        ItemStack sinterOutput = new ItemStack(ModItems.SINTERED_IRON.get());

        ItemStack remSinter = inventory.insertItem(3, sinterOutput, true);

        // 出力に完成品を受け入れるスペースが1個分以上あればtrue
        return remSinter.isEmpty();
    }

    private boolean hasCokeFuel() {
        ItemStack fuel = inventory.getStackInSlot(2);
        if (fuel.isEmpty())
            return false;

        // スロット2に入っているのがコークかチェック
        return fuel.is(ModItems.COKE.get());
    }

    private void consumeCoke() {
        inventory.getStackInSlot(2).shrink(1);
        // コーク1個あたり 1600 tick (80秒間) 燃焼させる設定
        this.burnTime = 1600;
        this.maxBurnTime = 1600;
        setChanged();
    }

    private void stopProgress() {
        if (this.progress > 0) {
            // 条件（材料切れや停電）が途切れたら、1Tickにつき2ずつゲージが冷えて減少する
            this.progress = Math.max(0, this.progress - 2);
            setChanged();
        }
    }

    private void completeSintering() {
        // 原料（鉄精鉱、石灰岩）をそれぞれ1つずつ正式消費
        inventory.getStackInSlot(0).shrink(1);
        inventory.getStackInSlot(1).shrink(1);

        // 製品をそれぞれの指定スロットへ実投入
        inventory.insertItem(3, new ItemStack(ModItems.SINTERED_IRON.get()), false); // 焼結鉱

        this.progress = 0;
        setChanged();
    }

    // --- NBT保存・読み込み処理 ---
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("Inventory", inventory.serializeNBT());
        pTag.putInt("Energy", energyStorage.getEnergyStored());
        pTag.putInt("Progress", progress);
        pTag.putInt("BurnTime", burnTime);
        pTag.putInt("MaxBurnTime", maxBurnTime);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inventory.deserializeNBT(pTag.getCompound("Inventory"));
        int storedEnergy = pTag.getInt("Energy");
        energyStorage.extractEnergy(energyStorage.getMaxEnergyStored(), false);
        energyStorage.receiveEnergy(storedEnergy, false);
        progress = pTag.getInt("Progress");
        burnTime = pTag.getInt("BurnTime");
        maxBurnTime = pTag.getInt("MaxBurnTime");
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY)
            return energyCapability.cast();
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemCapability.cast();
        return super.getCapability(cap, side);
    }

    // --- Container用の実装 ---
    @Override public int getContainerSize() { return inventory.getSlots(); }
    @Override public boolean isEmpty() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }
    @Override public ItemStack getItem(int index) {
        return inventory.getStackInSlot(index); }
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

    @Override public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }
    @Override public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId()); }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SinteringFurnaceMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}