package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.DustCollectorBlockEntity;
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

public class DustCollectorMenu extends AbstractContainerMenu {
    private final DustCollectorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // クライアント側（クライアントがパケットを受け取ってMenuを初期化する時用）
    public DustCollectorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    // サーバー側（BlockEntityから直接Menuを開く時用）
    public DustCollectorMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.DUST_COLLECTOR_MENU.get(), pContainerId);
        checkContainerDataCount(data, 2);
        this.blockEntity = (DustCollectorBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // 機械側のインベントリ（スロット）を追加
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 入力スロット (インデックス0) -> 座標 (56, 35)
            this.addSlot(new SlotItemHandler(handler, 0, 56, 35));
            // 出力スロット1 (インデックス1) -> 座標 (116, 17)
            this.addSlot(new SlotItemHandler(handler, 1, 116, 17));
            // 出力スロット2 (インデックス2) -> 座標 (116, 53)
            this.addSlot(new SlotItemHandler(handler, 2, 116, 53));
        });

        // プレイヤーインベントリの追加 (Y=84 から 3行分)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // プレイヤーホットバーの追加 (Y=142)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }

        // データの同期登録（0: Progress, 1: Energy）
        addDataSlots(data);
    }

    // ゲージ同期用のアクセサ
    public int getProgress() { return this.data.get(0); }
    public int getEnergy() { return this.data.get(1); }
    public int getMaxEnergy() { return 36000; } // 最大貯蔵量固定

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        // クイックムーブ（シフトクリック移動）の安全処理
        if (pIndex < 3) {
            // 機械側スロット(0~2)からプレイヤーインベントリへ
            if (!this.moveItemStackTo(sourceStack, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // プレイヤー側から機械の入力スロット(0)へ
            if (!this.moveItemStackTo(sourceStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);
        return copyStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.DUSTCOLLECTOR.get());
    }
}