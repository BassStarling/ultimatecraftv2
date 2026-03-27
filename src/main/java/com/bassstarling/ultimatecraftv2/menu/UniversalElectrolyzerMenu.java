package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.UniversalElectrolyzerBlockEntity;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class UniversalElectrolyzerMenu extends AbstractContainerMenu {
    private final UniversalElectrolyzerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public UniversalElectrolyzerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public UniversalElectrolyzerMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.UNIVERSALELECTROLYZER_MENU.get(), pContainerId);
        checkContainerSize(inv, 2); // アイテムスロットは0(入力)と1(出力)の2つ
        this.blockEntity = (UniversalElectrolyzerBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // 機械側のスロット配置 (座標データに基づく)
        addMachineSlots();

        // プレイヤー側のインベントリ配置 (8, 90 / 8, 148)
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // データスロットの同期 (エネルギー、プログレス等)
        addDataSlots(data);
    }

    private void addMachineSlots() {
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 入力スロット (56, 38) -> index: 0
            this.addSlot(new SlotItemHandler(handler, 0, 56, 38));

            // 出力スロット (147, 38) -> index: 1
            this.addSlot(new SlotItemHandler(handler, 1, 147, 38) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }
            });
        });
    }

    public FluidStack getInputFluid() {
        return blockEntity.getInputFluid();
    }

    public FluidStack getOutputFluid() {
        return blockEntity.getOutputFluid();
    }

    // GUI描画用のデータ取得メソッド

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getProgress() {
        return this.data.get(2);
    }

    public int getMaxProgress() {
        return this.data.get(3);
    }

    public boolean isCrafting() {
        return getProgress() > 0;
    }

    // 矢印（プログレスバー）の幅計算用
    public int getScaledProgress(int width) {
        int progress = getProgress();
        int maxProgress = getMaxProgress();
        return maxProgress != 0 && progress != 0 ? progress * width / maxProgress : 0;
    }

    // プレイヤーインベントリの定型文

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 90 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 148));
        }
    }

    // シフトクリック時の挙動 (クイックムーブ)
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.UNIVERSAL_ELECTROLYZER.get());
    }
}