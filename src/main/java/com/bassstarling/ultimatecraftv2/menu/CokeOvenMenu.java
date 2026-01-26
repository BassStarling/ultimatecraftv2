package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.CokeOvenBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.SlotItemHandler;

public class CokeOvenMenu extends AbstractContainerMenu {
    private final CokeOvenBlockEntity blockEntity;
    private final ContainerData data;

    public CokeOvenMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.COKE_OVEN_MENU.get(), pContainerId);
        this.blockEntity = (CokeOvenBlockEntity) entity;
        this.data = data;

        // スロット配置 (GUI画像に基づき座標を調整してください)
// CokeOvenMenu.java
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 56, 17));  // 原料
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 56, 53));  // 燃料
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 116, 22)); // 出力（画像では上の方にあるため修正）
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 116, 47)); // バケツ（出力の真下）

        layoutPlayerInventorySlots(inv, 8, 84); // プレイヤーインベントリ
        addDataSlots(data); // progress等の同期用
    }

    // 進捗ゲージ用
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowSize = 24; // 矢印のテクスチャ幅
        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    // 液体ゲージ用 (0-16pxなど)
    public int getScaledFluid() {
        int fluid = this.data.get(2);
        int capacity = this.data.get(3);
        int tankHeight = 50; // タンクの表示高さ
        return capacity != 0 ? (int)((long)fluid * tankHeight / capacity) : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // 0:原料, 1:燃料, 2:出力, 3:バケツ
            if (index < 4) {
                // ブロックのインベントリからプレイヤーのインベントリへ移動
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // プレイヤーのインベントリからブロックへ移動
                if (itemstack1.is(Items.COAL)) {
                    // 原料スロット(0)へ
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) return ItemStack.EMPTY;
                } else if (ForgeHooks.getBurnTime(itemstack1, RecipeType.SMELTING) > 0) {
                    // 燃料スロット(1)へ
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) return ItemStack.EMPTY;
                } else if (itemstack1.is(Items.BUCKET)) {
                    // バケツスロット(3)へ
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) return ItemStack.EMPTY;
                } else if (index < 31) {
                    // インベントリからホットバーへ
                    if (!this.moveItemStackTo(itemstack1, 31, 40, false)) return ItemStack.EMPTY;
                } else {
                    // ホットバーからインベントリへ
                    if (!this.moveItemStackTo(itemstack1, 4, 31, false)) return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, ModBlocks.COKEOVEN.get());
    }

    private int addSlotRange(Container playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    // 進捗(progress)は前回作成済みであれば重複に注意してください

    // 燃料が燃えているかどうか（burnTime > 0）
    public boolean isBurning() {
        return this.data.get(4) > 0;
    }

    // 炎のアイコンをどれくらい削るか（0〜13px程度）
    public int getBurnProgress() {
        int burnTime = this.data.get(4);
        int totalBurnTime = this.data.get(5);
        int fireIconSize = 13; // 炎のテクスチャの高さ

        return totalBurnTime != 0 && burnTime != 0 ? burnTime * fireIconSize / totalBurnTime : 0;
    }

    private int addSlotBox(Container playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(Container playerInventory, int leftCol, int topRow) {
        // プレイヤーのメインインベントリ (3段 x 9列)
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // プレイヤーのホットバー (1段 x 9列)
        topRow += 58; // メインインベントリとの間隔
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}