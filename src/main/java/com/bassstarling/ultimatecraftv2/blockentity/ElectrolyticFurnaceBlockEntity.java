package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.menu.ElectrolyticFurnaceMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ElectrolyticFurnaceBlockEntity extends BlockEntity implements MenuProvider {



    private final ItemStackHandler items = new ItemStackHandler(3) {

        @Override
        protected void onContentsChanged(int slot) {
            if (!level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            setChanged();
        }
    };



    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide ? null :
                (lvl, pos, st, be) -> {
                    if (be instanceof ElectrolyticFurnaceBlockEntity furnace) {
                        ElectrolyticFurnaceBlockEntity.tick(lvl, pos, st, furnace);
                    }
                };
    }



    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", items.serializeNBT());
        tag.putInt("Progress", progress);
        tag.putString("State", internalState.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("Items"));
        progress = tag.getInt("Progress");
        internalState = FurnaceState.valueOf(tag.getString("State"));
    }



    private int progress = 0;
    private static final int MAX_PROGRESS = 20 * 20;

    public int getProgress() { return progress; }
    public int getMaxProgress() { return MAX_PROGRESS; }



    public ElectrolyticFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTROLYTIC_FURNACE.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return items;
    }



    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> (internalState == FurnaceState.PREPARING)? PREPARE_TIME : MAX_PROGRESS;
                case 2 -> internalState.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) progress = value;
            if (index == 2) internalState = FurnaceState.values()[value];
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public ContainerData getData() {
        return data;
    }



    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ElectrolyticFurnaceMenu(id, inventory, this);
    }



    public static void tick(Level level, BlockPos pos, BlockState state, ElectrolyticFurnaceBlockEntity be) {
        if (level.isClientSide) return;

        switch (be.internalState) {
            case EMPTY -> be.InEmpty();
            case MOLTEN_CRYOLITE -> be.AddAlumina();
            case PREPARING -> be.prepare(level, pos);
            case RESETTING -> be.forceResetProgress(level, pos);
            case PROCESSING -> be.processElectrolysis(level, pos);
        }
    }



    private void InEmpty() {
        ItemStack input = items.getStackInSlot(0);
        ItemStack spark = items.getStackInSlot(1);
        ItemStack output = items.getStackInSlot(2);

        if (!output.isEmpty()) return;
        if (!input.is(ModItems.CRYOLITE.get())) return;
        if (!(spark.getItem() instanceof SparkStone)) return;

        if (SparkStone.getTier(spark) < 5) return;

        items.extractItem(0, 1, false);
        items.extractItem(1, 1, false);

        ItemStack moltenCryolite = new ItemStack(ModItems.MOLTEN_CRYOLITE.get());
        items.setStackInSlot(2, moltenCryolite);

        internalState = FurnaceState.MOLTEN_CRYOLITE;
        setChanged();
    }

    private void AddAlumina() {
        if (progress > 0) return;

        ItemStack input = items.getStackInSlot(0);
        if (!input.is(ModItems.ALUMINA.get())) return;

        input.shrink(1);

        // 0からスタートさせることで、次のティックのprepareで1になる
        this.progress = 0;
        this.internalState = FurnaceState.PREPARING;
        setChanged();
    }

    private static final int PREPARE_TIME = 10;

    private void prepare(Level level, BlockPos pos) {
        progress++;

        if (progress < PREPARE_TIME) {
            setChanged();
            return;
        }

        items.setStackInSlot(2, new ItemStack(ModItems.MOLTEN_CRYOLITE_WITH_ALUMINA.get()));

        // 重要：直接 forceResetProgress を呼ばない
        // 状態をRESETTINGにし、progressを0にして一旦処理を抜ける
        // これにより、このティックの終わりに「progress=0」がクライアントへ同期される
        this.progress = 0;
        this.internalState = FurnaceState.RESETTING;
        setChanged();
    }

    private void forceResetProgress(Level level, BlockPos pos) {
        ItemStack slot0 = items.getStackInSlot(0); // 通常は空であるはず（投入済みのため）
        ItemStack spark = items.getStackInSlot(1); // スパークストーン
        ItemStack slot2 = items.getStackInSlot(2); // 中間生成物

        // 1. スロット2に正しい中間生成物が入っているかチェック
        // 前の工程で作られた「融解した氷晶石とアルミナ」がスロット2にあることが必須条件です
        boolean hasMaterial = slot2.is(ModItems.MOLTEN_CRYOLITE_WITH_ALUMINA.get());

        // 2. スロット0が空であるかチェック（仕様に合わせて変更可能）
        // アルミナが消費された後のため、スロット0が空であることを開始条件に加えます
        boolean isInputEmpty = slot0.isEmpty();

        // 3. スパークストーンのチェック
        boolean hasValidSpark =!spark.isEmpty() && spark.getItem() instanceof SparkStone && SparkStone.getTier(spark) >= 6;

        // すべての条件を満たした場合のみ、第3段階（PROCESSING）を開始する
        if (hasMaterial && isInputEmpty && hasValidSpark) {

            // アイテムを消費
            items.extractItem(1, 1, false);

            this.progress = 0;
            this.internalState = FurnaceState.PROCESSING;

            // クライアント側（GUI）へ更新を通知
            level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
            setChanged(); // データの変更をマーク
        } else {
            // 条件を満たしていない場合は、RESETTING状態のまま待機
            // 矢印も動かず、ブロックも置き換わりません
        }
    }


    private void processElectrolysis(Level level, BlockPos pos) {
        // 処理中のチェック（念のため）
        if (this.internalState!= FurnaceState.PROCESSING) return;

        progress++;

        if (progress < MAX_PROGRESS) {
            setChanged();
            return;
        }

        // 400ティック完了後の処理
        for (int i = 0; i < items.getSlots(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }

        progress = 0;
        internalState = FurnaceState.EMPTY;

        // ブロックをUSEDに置き換え
        level.setBlock(pos, ModBlocks.USED_ELECTROLYTICFURNACE.get().defaultBlockState(), 3);
        setChanged();
    }



    @Override
    public Component getDisplayName() {
        return Component.translatable("container.ultimatecraftv2.electrolytic_furnace");
    }

    private FurnaceState internalState = FurnaceState.EMPTY;

    public enum FurnaceState {
        EMPTY,
        MOLTEN_CRYOLITE,
        PREPARING,
        RESETTING,
        PROCESSING
    }
}
