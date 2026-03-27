package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.PrecipitatorBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class PrecipitatorMenu extends AbstractContainerMenu {
    private final PrecipitatorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public PrecipitatorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public PrecipitatorMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.PRECIPITATOR_MENU.get(), containerId);
        checkContainerSize(inv, 1);
        this.blockEntity = (PrecipitatorBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // 1. 機械のスロットを追加 (Index 0) - 座標: 93, 38
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 93, 38));
        });

        // 2. プレイヤーインベントリを追加 (Index 1-27) - 座標: 7, 89
        addPlayerInventory(inv);
        // 3. ホットバーを追加 (Index 28-36) - 座標: 7, 147
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    public boolean isProcessing() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // 矢印の最大幅

        return (maxProgress != 0 && progress != 0) ? (progress * progressArrowSize / maxProgress) : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        // index 0: 機械の出力スロット
        // index 1-27: プレイヤーインベントリ
        // index 28-36: ホットバー
        if (index < 1) {
            // 機械からプレイヤーへ移動 (36 = 1+27+8 ではなく、インベントリ全体の範囲 1 to 37)
            if (!this.moveItemStackTo(sourceStack, 1, 37, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // プレイヤーから機械へ移動 (今回は入力スロットがないため、基本は何もしない)
            // もし将来的にアイテムを入れる場合はここにロジックを書く
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.PRECIPITATOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                // 指定座標 (7, 89) に合わせて配置
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 90 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            // 指定座標 (7, 147) に合わせて配置
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 148));
        }
    }

    public PrecipitatorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}