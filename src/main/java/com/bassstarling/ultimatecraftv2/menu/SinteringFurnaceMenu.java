package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.SinteringFurnaceBlockEntity;
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

public class SinteringFurnaceMenu extends AbstractContainerMenu {
    private final SinteringFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public SinteringFurnaceMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public SinteringFurnaceMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.SINTERING_FURNACE_MENU.get(), pContainerId);
        checkContainerDataCount(data, 4);
        this.blockEntity = (SinteringFurnaceBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // 1. 焼結炉側のインベントリ配置 (計4スロット)
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 47, 17));  // 0: 鉄精鉱
            this.addSlot(new SlotItemHandler(handler, 1, 65, 17));  // 1: 石灰岩
            this.addSlot(new SlotItemHandler(handler, 2, 56, 53));  // 2: コーク
            this.addSlot(new SlotItemHandler(handler, 3, 116, 35)); // 3: 焼結鉱（出力）
        });

        // 2. プレイヤーインベントリ (8, 84)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 3. プレイヤーホットバー (8, 142)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    // Screenからゲージの進捗を取得するためのメソッド
    public int getProgress() { return this.data.get(0); }
    public int getEnergy() { return this.data.get(1); }
    public int getBurnTime() { return this.data.get(2); }
    public int getMaxBurnTime() { return this.data.get(3); }
    public int getMaxEnergy() { return 36000; }

    public boolean isBurning() { return this.getBurnTime() > 0; }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        if (pIndex < 4) {
            // 焼結炉(0~3) -> プレイヤーインベントリへ
            if (!this.moveItemStackTo(sourceStack, 4, 40, true)) {
                return ItemStack.EMPTY;
            } sourceSlot.onQuickCraft(sourceStack, copyStack);
        } else {
            // プレイヤーインベントリ -> 焼結炉の入力側(0~2)へ自動振り分け試行
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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.SINTERINGFURNACE.get());
    }
}