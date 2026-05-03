package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.BatteryChargerBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class BatteryChargerMenu extends AbstractContainerMenu {
    // スロットの定数定義（ミスを防ぐため）
    private static final int CHARGER_SLOT = 0;
    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_HOTBAR_START = 28;
    private static final int PLAYER_INVENTORY_END = 37; // 全スロット数は37

    private final BatteryChargerBlockEntity blockEntity;
    private final ContainerData data;

    public BatteryChargerMenu(int windowId, Inventory inv, FriendlyByteBuf extraData) {
        this(windowId, inv,extraData.readBlockPos());
    }

    // 内部呼び出し用の中間コンストラクタ
    private BatteryChargerMenu(int windowId, Inventory inv, BlockPos pos) {
        this(windowId, inv,
                inv.player.level().getBlockEntity(pos),
                getSafeContainerData(inv.player.level(), pos));
    }

    // 安全に ContainerData を取得するためのヘルパーメソッド
    private static ContainerData getSafeContainerData(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BatteryChargerBlockEntity charger) {
            return charger.getContainerData(); // さきほど作ったメソッド
        }
        // もしBlockEntityが見つからない場合は、クラッシュ防止に空のデータを返す
        return new SimpleContainerData(1);
    }

    public BatteryChargerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BATTERY_CHARGER_MENU.get(), containerId);
        this.blockEntity = (BatteryChargerBlockEntity) entity;
        this.data = data;

        // 1. 充電器スロット (index 0)
        // ここで handler が正しく取得できているか確認
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addSlot(new SlotItemHandler(handler, 0, 80, 34) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    // UVライト本体の投入を禁止し、電池やスパークストーンのみ許可する
                    // ここに ModItems.UV_LIGHT.get() ではない判定を入れます
                    return stack.getItem() != ModItems.UV_LIGHT.get() &&
                            (stack.getItem() == ModItems.LITHIUM_ION_BATTERY.get() ||
                                    stack.getItem() == ModItems.SPARK_STONE.get());
                }
            });
        });

        // 2. プレイヤーインベントリ (index 1 ~ 27)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 3. ホットバー (index 28 ~ 36)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == CHARGER_SLOT) {
                // 充電器からプレイヤーインベントリへ
                if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END) {
                // プレイヤーインベントリから充電器へ
                if (!this.moveItemStackTo(itemstack1, CHARGER_SLOT, CHARGER_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
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

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, ModBlocks.BATTERYCHARGER.get());
    }

    public int getEnergy() {
        // data.get(0) は BlockEntity から同期された energyBuffer の値（またはその1/10）を返します
        return this.data.get(0);
    }
}