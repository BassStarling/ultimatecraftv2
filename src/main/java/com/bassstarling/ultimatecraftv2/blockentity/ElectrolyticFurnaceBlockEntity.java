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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import static net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput;

public class ElectrolyticFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler items = new ItemStackHandler(3) {
        private boolean isValidInput(ItemStack stack) {
            if (stack.isEmpty()) return false;

            return switch (internalState) {
                case EMPTY ->
                        stack.is(ModItems.CRYOLITE.get());

                case MOLTEN_CRYOLITE ->
                        stack.is(ModItems.ALUMINA.get());

                case PROCESSING ->
                        false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        }
    };

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
    private static final int MAX_PROGRESS = 20 * 20; // 20秒

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
                case 1 -> MAX_PROGRESS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) progress = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ElectrolyticFurnaceMenu(id, inventory, this, data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectrolyticFurnaceBlockEntity be) {
        if (level.isClientSide) return;

        switch (be.internalState) {
            case EMPTY -> be.InEmpty();
            case MOLTEN_CRYOLITE -> be.AddAlumina();
            case PROCESSING -> be.processElectrolysis(level, pos);
        }
    }

    private void InEmpty() {
        ItemStack input = items.getStackInSlot(0);
        ItemStack spark = items.getStackInSlot(1);
        ItemStack output = items.getStackInSlot(2);

        // 出力スロットが空でないなら何もしない
        if (!output.isEmpty()) return;

        // 入力チェック
        if (input.isEmpty() || spark.isEmpty()) return;

        // 氷晶石チェック
        if (input.getItem() != ModItems.CRYOLITE.get()) return;

        // スパークストーンチェック
        if (!(spark.getItem() instanceof SparkStone)) return;

        // Tier チェック
        int tier = SparkStone.getTier(spark);
        if (tier < 5) return;

        // --- ここまで来たら成立 ---

        // 消費
        items.extractItem(0, 1, false);
        items.extractItem(1, 1, false);

        // 出力生成
        ItemStack moltenCryolite = new ItemStack(ModItems.MOLTEN_CRYOLITE.get());
        items.setStackInSlot(2, moltenCryolite);

        // 状態遷移
        internalState = FurnaceState.MOLTEN_CRYOLITE;

        setChanged();
    }

    private void AddAlumina() {
        ItemStack input = items.getStackInSlot(0);

        // アルミナが入っていなければ何もしない
        if (input.isEmpty()) return;
        if (!input.is(ModItems.ALUMINA.get())) return;

        // アルミナを1個消費
        input.shrink(1);
        items.setStackInSlot(0, ItemStack.EMPTY);

        // 電解処理へ移行
        internalState = FurnaceState.PROCESSING;
        progress = 0;

        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.ultimatecraftv2.electrolytic_furnace");
    }

    public enum FurnaceState {
        EMPTY,
        MOLTEN_CRYOLITE,
        PROCESSING
    }
    private void processElectrolysis(Level level, BlockPos pos) {
        progress++;

        if (progress < MAX_PROGRESS) {
            return;
        }

        // ===== 加工完了 =====

        // 全スロット消費
        for (int i = 0; i < items.getSlots(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }

        // 状態リセット
        progress = 0;
        internalState = FurnaceState.EMPTY;

        // 使用済み電解炉に置換
        level.setBlock(
                pos,
                ModBlocks.USED_ELECTROLYTICFURNACE.get().defaultBlockState(),
                3
        );
    }

    private FurnaceState internalState = FurnaceState.EMPTY;

    private boolean isValidSparkStone(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // スパークストーンでなければ不可
        if (!(stack.getItem() instanceof SparkStone)) return false;

        // Tierを取得
        int tier = SparkStone.getTier(stack);

        // EMPTY / MOLTEN_CRYOLITE では Tier5以上を許可
        return switch (internalState) {
            case EMPTY, MOLTEN_CRYOLITE -> tier >= 5;
            case PROCESSING -> tier >= 7;
        };
    }
}