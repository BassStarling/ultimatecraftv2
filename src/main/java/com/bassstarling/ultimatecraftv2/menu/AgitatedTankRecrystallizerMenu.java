package com.bassstarling.ultimatecraftv2.menu;

import com.bassstarling.ultimatecraftv2.blockentity.AgitatedTankRecrystallizerBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class AgitatedTankRecrystallizerMenu extends AbstractContainerMenu {
    private final AgitatedTankRecrystallizerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public AgitatedTankRecrystallizerBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
    // クライアント側（Screen）から呼び出されるコンストラクタ
    public AgitatedTankRecrystallizerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
    }

    // サーバー側（BlockEntity）から呼び出されるコンストラクタ
    public AgitatedTankRecrystallizerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.AGITATEDTANKRECRYSTALLIZER_MENU.get(), containerId);
        checkContainerSize((Container) entity, 2);
        this.blockEntity = (AgitatedTankRecrystallizerBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        // BlockEntityのインベントリをスロットに配置
        addSlot(new SlotItemHandler(blockEntity.inventory, 0, 56, 38)); // 入力（種）
        addSlot(new SlotItemHandler(blockEntity.inventory, 1, 147, 38)); // 出力（結晶）

        // プレイヤーのインベントリとホットバーの配置
        layoutPlayerInventorySlots(inv, 8, 90);

        // 数値データの同期
        addDataSlots(data);
    }

    // --- 同期データの取得用メソッド（Screenから呼ばれる） ---
    public int getProgressScaled(int pixels) {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        return maxProgress != 0 && progress != 0 ? progress * pixels / maxProgress : 0;
    }

    public int getSparkScaled(int pixels) {
        int spark = data.get(2);
        int maxSpark = data.get(3);
        return maxSpark != 0 ? (int) ((float) spark / maxSpark * pixels) : 0;
    }

    public float getTemperature() {
        return data.get(4) / 100.0f; // 100倍して送られてきたintをfloatに戻す
    }

    public int getEfficiency() {
        return data.get(5);
    }

    // パケットから値を更新するためのGetter（ScreenのsetValue用）
    public int getUserInputFlow() { return blockEntity.getUserInputFlow(); }
    public int getUserInputPower() { return blockEntity.getUserInputPower(); }

    // --- 基本メソッド ---
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.AGITATEDTANKRECRYSTALLIZER.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // シフトクリック時の挙動（スロット移動ロジック）をここに記述
        // 基本的には標準的な実装を流用します
        return ItemStack.EMPTY;
    }

    private void layoutPlayerInventorySlots(Inventory inv, int x, int y) {
        // インベントリ(3x9)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        // ホットバー(1x9)
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, x + k * 18, 148));
        }
    }

    public net.minecraftforge.fluids.FluidStack getInputFluid() {
        return blockEntity.inputTank.getFluid();
    }

    public net.minecraftforge.fluids.FluidStack getOutputFluid() {
        return blockEntity.outputTank.getFluid();
    }
}