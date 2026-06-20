package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.OxidizerBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class OxidizerMenu extends AbstractContainerMenu {
    private final OxidizerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // クライアント側（パケットを受信して初期化する用）
    public OxidizerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(1));
    }

    // サーバー側（BlockEntityから直接開く用）
    public OxidizerMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.OXIDIZER_MENU.get(), pContainerId);
        checkContainerDataCount(data, 1); // 同期する数値はprogressの1つのみ
        this.blockEntity = (OxidizerBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // 酸化機のインベントリスロット設定
        this.blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 56, 17));
            this.addSlot(new SlotItemHandler(handler, 1, 56, 53));
            this.addSlot(new SlotItemHandler(handler, 2, 114, 17));
            this.addSlot(new SlotItemHandler(handler, 3, 114, 53));
        });

        // プレイヤーインベントリの設定
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // プレイヤーホットバーの設定
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }

        // ゲージ同期データの登録（0:進捗のみ）
        addDataSlots(data);
    }

    // Screen（画面描画側）から参照するためのアクセサメソッド
    public int getProgress() {
        return this.data.get(0);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        // クイックムーブ（シフトクリック移動）の制御
        if (pIndex < 4) {
            if (!this.moveItemStackTo(sourceStack, 4, 40, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // プレイヤーインベントリからマシン側(0~1)への自動投入ロジック（スロット2,3は出力なので入れない）
            if (!this.moveItemStackTo(sourceStack, 0, 2, false)) {
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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.OXIDIZER.get());
    }
}