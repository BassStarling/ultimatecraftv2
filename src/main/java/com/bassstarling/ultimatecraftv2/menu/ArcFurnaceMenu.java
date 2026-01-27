package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.ArcFurnaceBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class ArcFurnaceMenu extends AbstractContainerMenu {
    private final ArcFurnaceBlockEntity blockEntity;
    private final Level level;
    public final ContainerData data;

    public ArcFurnaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public ArcFurnaceMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ARC_FURNACE_MENU.get(), containerId);

        if (entity instanceof ArcFurnaceBlockEntity furnaceEntity) {
            this.blockEntity = furnaceEntity;
        } else {
            throw new IllegalStateException("BlockEntity is not ArcFurnaceBlockEntity!");
        }

        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 56, 17));  // Input
            this.addSlot(new SlotItemHandler(handler, 2, 56, 53));  // Spark
            this.addSlot(new SlotItemHandler(handler, 1, 116, 35)); // Output
        });

        addDataSlots(data);
    }

    // クライアント側でゲージ計算に使うメソッド
    public int getScaledEnergy() {
        int energy = this.data.get(0);
        int maxEnergy = this.data.get(1);
        int barHeight = 50;
        return maxEnergy != 0 && energy != 0 ? energy * barHeight / maxEnergy : 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(2);
        int maxProgress = this.data.get(3);
        int arrowWidth = 24;
        return maxProgress != 0 && progress != 0 ? progress * arrowWidth / maxProgress : 0;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.ARC_FURNACE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        // プレイヤーのインベントリ(index 0-35)から機械(index 36-38)へ
        if (index < 36) {
            // スパークストーンなら電力スロット(37)へ、それ以外なら入力スロット(36)へ
            if (sourceStack.is(ModItems.SPARK_STONE.get())) {
                if (!moveItemStackTo(sourceStack, 37, 38, false)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(sourceStack, 36, 37, false)) return ItemStack.EMPTY;
            }
        }
        // 機械(index 36-38)からプレイヤーへ
        else {
            if (!moveItemStackTo(sourceStack, 0, 36, true)) return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();

        return copyStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
