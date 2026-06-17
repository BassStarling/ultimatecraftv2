package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.EthylenePlantBlockEntity;
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

public class EthylenePlantMenu extends AbstractContainerMenu {
    private final EthylenePlantBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // クライアント側（パケットを受信して初期化する用）
    public EthylenePlantMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    // サーバー側（BlockEntityから直接開く用）
    public EthylenePlantMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        // ※ ModMenuTypesの登録名（ETHYLENE_PLANT_MENU）に合わせてください
        super(ModMenuTypes.ETHYLENE_PLANT_MENU.get(), pContainerId);
        checkContainerDataCount(data, 4);
        this.blockEntity = (EthylenePlantBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // 1. エチレンプラントのインベントリスロット設定
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // スロット0 (ナフサバケツ) -> 座標 (47, 17)
            this.addSlot(new SlotItemHandler(handler, 0, 47, 17));
            // スロット1 (水バケツ) -> 座標 (65, 17)
            this.addSlot(new SlotItemHandler(handler, 1, 65, 17));
            // スロット2 (燃料) -> 座標 (56, 53)
            this.addSlot(new SlotItemHandler(handler, 2, 56, 53));
            // スロット3 (エチレン製品) -> 座標 (116, 21)
            this.addSlot(new SlotItemHandler(handler, 3, 116, 21));
            // スロット4 (空バケツ搬出) -> 座標 (116, 48)
            this.addSlot(new SlotItemHandler(handler, 4, 116, 48));
        });

        // 2. プレイヤーインベントリの設定 -> 座標 (8, 84) から 9列×3行
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 3. プレイヤーホットバーの設定 -> 座標 (8, 142) から 9列×1行
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }

        // ゲージ同期データの登録（0:進捗, 1:エネルギー, 2:燃焼残り, 3:燃料最大）
        addDataSlots(data);
    }

    // Screen（画面描画側）から参照するためのアクセサメソッド群
    public int getProgress() { return this.data.get(0); }
    public int getEnergy() { return this.data.get(1); }
    public int getBurnTime() { return this.data.get(2); }
    public int getMaxBurnTime() { return this.data.get(3); }
    public int getMaxEnergy() { return 36000; } // BlockEntityで設定した最大容量36,000 FE

    // 炎ゲージ（バーニング）が有効かどうか
    public boolean isBurning() { return this.getBurnTime() > 0; }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        // クイックムーブ（シフトクリック移動）の制御
        if (pIndex < 5) {
            // マシン側のスロット(0~4)からプレイヤーインベントリ(5~40)へ退避
            if (!this.moveItemStackTo(sourceStack, 5, 41, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // プレイヤーインベントリからマシン側への自動投入ロジック（まずは入力スロットへ試行）
            if (!this.moveItemStackTo(sourceStack, 0, 3, false)) {
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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.ETHYLENE_PLANT.get());
    }
}