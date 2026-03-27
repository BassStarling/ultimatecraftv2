package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.FilterBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FilterMenu extends AbstractContainerMenu {
    private final FilterBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public FilterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public FilterMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.FILTER_MENU.get(), containerId);
        checkContainerSize(inv, 1); // 赤い泥アイテム用スロットがある場合
        this.blockEntity = (FilterBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // もし「赤い泥」をアイテムとして取り出すスロットを設置する場合（例: x=100, y=50）
        // this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 100, 50));

        // クライアント側へ進行度データを同期
        addDataSlots(data);
    }

    public boolean isFiltering() {
        return data.get(0) > 0;
    }

    public int getScaledProgress(int arrowWidth) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return maxProgress != 0 && progress != 0 ? progress * arrowWidth / maxProgress : 0;
    }

    // 液体タンクをScreenから参照するためのゲッター
    public FilterBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        // シフトクリック時の挙動（基本は標準的な実装でOK）
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.FILTER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        // プレイヤーインベントリ (x=8, y=90)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 90 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        // ホットバー (x=8, y=148)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 148));
        }
    }
}
