package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.CastingMachineBlockEntity;
import com.bassstarling.ultimatecraftv2.blockentity.ElectrolyticFurnaceBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CastingMachineMenu extends AbstractContainerMenu {

    private final CastingMachineBlockEntity blockEntity;
    private final ContainerData data;

    // A: クライアント側（メニューを開く時）の呼び出し用
    public CastingMachineMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, (CastingMachineBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    // B: サーバー側（BlockEntityから生成される時）の呼び出し用
    public CastingMachineMenu(int pContainerId, Inventory inv, CastingMachineBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CASTING_MACHINE_MENU.get(), pContainerId);
        checkContainerSize(inv, 3);
        this.blockEntity = entity;
        this.data = data;

        // アイテムハンドラーの登録
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 56, 17)); // 入力1
            this.addSlot(new SlotItemHandler(handler, 1, 56, 53)); // 入力2
            this.addSlot(new SlotItemHandler(handler, 2, 116, 35)); // 出力
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(
                        inv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(
                    inv,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return empty;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        // ブロック → プレイヤー
        if (index < 3) {
            if (!this.moveItemStackTo(stack, 3, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }
        // プレイヤー → ブロック
        else {
            if (!this.moveItemStackTo(stack, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    // 進捗（矢印の伸び）を取得するメソッド
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowSize = 24; // テクスチャの矢印の幅
        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /* ========= 進捗同期 ========= */
    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public boolean isProcessing() {
        return data.get(2) == ElectrolyticFurnaceBlockEntity.FurnaceState.PROCESSING.ordinal();
    }
}