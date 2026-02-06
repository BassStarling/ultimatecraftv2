package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.IndustrialWorkbenchBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class IndustrialWorkbenchMenu extends AbstractContainerMenu {
    private final IndustrialWorkbenchBlockEntity blockEntity;
    private final Level level;

    // クライアント側（パケットから生成）
    public IndustrialWorkbenchMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public IndustrialWorkbenchMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.INDUSTRIAL_WORKBENCH_MENU.get(), containerId);
        this.blockEntity = (IndustrialWorkbenchBlockEntity) entity;
        this.level = inv.player.level();

        // 1. プレイヤーインベントリ (テクスチャに合わせて配置)
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // 2. 作業台スロット
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            int slotSize = 18;
            // 5x5グリッドの位置調整
            int startX = 30;
            int startY = 17;

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    this.addSlot(new SlotItemHandler(handler, col + row * 5, startX + col * slotSize, startY + row * slotSize));
                }
            }

            // ★完成品スロットの位置を右へ修正 (144 -> 160)
            this.addSlot(new ResultSlot(handler, 25, 160, 53, this.blockEntity));
        });
    }

    private void addPlayerInventory(Inventory playerInventory) {
        // Xを21から26へ動かすことで、テクスチャの枠線とスロットの隙間を均一にします
        // Yは124から126へ下げて、「インベントリ」の文字との間隔を空けます
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 26 + l * 18, 120 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        // インベントリの縦ライン(X=26)に合わせます
        // Yはテクスチャ下の独立した枠に合わせて184に調整
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 26 + i * 18, 178));
        }
    }

    // --- ロジック ---
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.INDUSTRIAL_WORKBENCH.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack sourceStack = ItemStack.EMPTY;
        Slot sourceSlot = this.slots.get(index);

        if (sourceSlot != null && sourceSlot.hasItem()) {
            ItemStack originalStack = sourceSlot.getItem();
            sourceStack = originalStack.copy();

            // 0-35: プレイヤーインベントリ
            // 36-60: 作業台入力
            // 61: 作業台出力
            if (index == 61) { // 出力スロットから
                if (!this.moveItemStackTo(originalStack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
                sourceSlot.onQuickCraft(originalStack, sourceStack);
            } else if (index < 36) { // インベントリから作業台へ
                if (!this.moveItemStackTo(originalStack, 36, 61, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // 作業台からインベントリへ
                if (!this.moveItemStackTo(originalStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                sourceSlot.set(ItemStack.EMPTY);
            } else {
                sourceSlot.setChanged();
            }
            sourceSlot.onTake(player, originalStack);
        }
        return sourceStack;
    }

    // --- 内部クラス: 完成品スロット専用 ---
    private static class ResultSlot extends SlotItemHandler {
        private final IndustrialWorkbenchBlockEntity entity;

        public ResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, IndustrialWorkbenchBlockEntity entity) {
            super(itemHandler, index, xPosition, yPosition);
            this.entity = entity;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                // 現在の盤面でバニラレシピが成立しているか再確認
                // (簡易的には、アイテムが入っているスロットを各1つ減らす)
                for (int i = 0; i < 25; i++) {
                    if (!handler.getStackInSlot(i).isEmpty()) {
                        handler.extractItem(i, 1, false);
                    }
                }
            });
            super.onTake(player, stack);
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        // サーバー側でのみ判定を実行
        if (!this.level.isClientSide) {
            this.blockEntity.updateRecipe();
        }
    }
}
