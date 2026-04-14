package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AutoCasterBlockEntity extends BlockEntity {
    // 1. FE (Forge Energy) 設定: 最大36,000 FE
    // receiveEnergyを外部（パイプ等）からも受け取れるように設定
    private final EnergyStorage energyStorage = new EnergyStorage(36000, 1000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) setChanged();
            return received;
        }
    };

    // 2. アイテム設定: 1スロットのみ（アルミニウム回収用）
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    // --- Capability 用の LazyOptional ---
    private LazyOptional<IEnergyStorage> energyOptional = LazyOptional.empty();
    private LazyOptional<IItemHandler> inventoryOptional = LazyOptional.empty();
    // 下面(DOWN)からの搬出専用（1スロット目のみ）
    private final LazyOptional<IItemHandler> outputOptional = LazyOptional.of(() -> new RangedWrapper(itemHandler, 0, 1));

    public AutoCasterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTO_CASTER_BE.get(), pos, state);
    }

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
        outputOptional.invalidate();
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // 1. 周囲のスパークストーンを吸収してFEに変換 (ポンプと同じロジック)
        if (level.getGameTime() % 20 == 0) { // 1秒に1回
            suctionSparkStones();
        }

        // 2. 隣接する使用済み電解炉をチェックしてメンテナンス
        if (level.getGameTime() % 10 == 0) { // 0.5秒に1回
            processMaintenance();
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

                // Tierに応じたエネルギー回復量（アーク炉の数値を参考に設定）
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

    private void processMaintenance() {
        // 全方位の隣接ブロックを確認
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            // ターゲットが「使用済み電解炉」であるかチェック
            if (neighborState.is(ModBlocks.USED_ELECTROLYTICFURNACE.get())) {

                // 実行条件: エネルギーが100FE以上、かつ内部インベントリにインゴットが入る空きがある
                ItemStack result = new ItemStack(ModItems.ALUMINIUM_INGOT.get());
                boolean hasEnergy = energyStorage.getEnergyStored() >= 100;
                boolean canInsert = itemHandler.insertItem(0, result, true).isEmpty();

                if (hasEnergy && canInsert) {
                    // 消費と生成
                    consumeEnergy(100);
                    itemHandler.insertItem(0, result, false);

                    // ブロックを元の「電解炉」に置き換え
                    level.setBlock(neighborPos, ModBlocks.ELECTROLYTICFURNACE.get().defaultBlockState(), 3);

                    // 完了演出（音など）
                    level.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.5f);
                    setChanged();
                    break; // 1回のtickで1つの炉のみ処理
                }
            }
        }
    }

    // FE消費用ヘルパー（EnergyStorageにはconsumeメソッドがないため）
    private void consumeEnergy(int amount) {
        // 内部的にエナジーを減らす処理
        // EnergyStorageを継承したカスタムクラスを作るか、
        // 以下のリフレクション的な手法、あるいは単純にオーバーライドで対応します
        this.energyStorage.extractEnergy(amount, false);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            // 下面(DOWN)からのアクセスのみ許可（搬出用）
            if (side == Direction.DOWN) {
                return outputOptional.cast();
            }
            // それ以外（上・横）は外部からのアクセスを遮断（empty）
            return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Inventory", itemHandler.serializeNBT());
        nbt.putInt("Energy", energyStorage.getEnergyStored());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        // EnergyStorageの値を復元
        int savedEnergy = nbt.getInt("Energy");
        this.energyStorage.receiveEnergy(savedEnergy, false);
    }

    public EnergyStorage getEnergyStorage() { return energyStorage; }
    public ItemStackHandler getItemHandler() { return itemHandler; }
}