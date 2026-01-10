package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.ElectrolyticFurnaceBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ElectrolyticFurnaceMenu extends AbstractContainerMenu {

    private final ElectrolyticFurnaceBlockEntity blockEntity;
    private final ContainerData data;

    public ElectrolyticFurnaceMenu(
            int id,
            Inventory playerInv,
            FriendlyByteBuf buf
    ) {
        this(
                id,
                playerInv,
                (ElectrolyticFurnaceBlockEntity)
                        playerInv.player.level().getBlockEntity(buf.readBlockPos())
        );
    }

    public ElectrolyticFurnaceMenu(
            int id,
            Inventory playerInv,
            ElectrolyticFurnaceBlockEntity blockEntity
    ) {
        super(ModMenuTypes.ELECTROLYTIC_FURNACE_MENU.get(), id);

        this.blockEntity = blockEntity;
        this.data = blockEntity.getData();

        this.addDataSlots(this.data);

        IItemHandler items = blockEntity.getItemHandler();

        // ===== 電解炉スロット =====
        this.addSlot(new SlotItemHandler(items, 0, 56, 17));
        this.addSlot(new SlotItemHandler(items, 1, 56, 53));
        this.addSlot(new SlotItemHandler(items, 2, 116, 35));

        // ===== プレイヤーインベントリ =====
        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
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

    public int getScaledProgress(int arrowWidth) {
        int progress = data.get(0);
        int maxProgress = data.get(1);

        if (maxProgress == 0) return 0;
        return progress * arrowWidth / maxProgress;
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

    public int getScaledProgress() {
        int progress = data.get(0);
        int max = data.get(1);
        int arrowWidth = 24;

        return max > 0 ? progress * arrowWidth / max : 0;
    }
}