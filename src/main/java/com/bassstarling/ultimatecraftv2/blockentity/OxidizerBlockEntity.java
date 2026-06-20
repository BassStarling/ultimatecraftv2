package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.block.OxidizerBlock;
import com.bassstarling.ultimatecraftv2.menu.OxidizerMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OxidizerBlockEntity extends BlockEntity implements MenuProvider, Container {

    // 4スロット構成 (0:二酸化硫黄, 1:酸素入り瓶, 2:三酸化硫黄出力, 3:空ビン搬出)
    public final ItemStackHandler inventory = new ItemStackHandler(4);
    private final LazyOptional<ItemStackHandler> itemCapability = LazyOptional.of(() -> inventory);

    protected final ContainerData data;

    private int progress = 0;
    private final int maxProgress = 800; // ⚠️触媒なしでじっくり待つため「40秒 (800tick)」に設定

    public OxidizerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.OXIDIZER_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return index == 0 ? OxidizerBlockEntity.this.progress : 0;
            }
            @Override
            public void set(int index, int value) {
                if (index == 0) OxidizerBlockEntity.this.progress = value;
            }
            @Override
            public int getCount() { return 1; } // 同期する数値は progress のみの「1つ」
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean isProcessing = hasMaterialsAndSpace();

        // 現在のブロックのLIT状態を取得
        boolean currentLit = this.getBlockState().getValue(OxidizerBlock.LIT);

        // 状態が変化したときだけブロックステートを更新（負荷軽減のため）
        if (isProcessing != currentLit) {
            level.setBlock(worldPosition, this.getBlockState().setValue(OxidizerBlock.LIT, isProcessing), 3);
        }

        if (isProcessing) {
            this.progress++;
            if (this.progress >= maxProgress) {
                completeOxidization();
            }
            setChanged();
        } else {
            if (this.progress > 0) {
                this.progress = 0;
                setChanged();
            }
        }
    }

    private boolean hasMaterialsAndSpace() {
        ItemStack sulfurDioxide = inventory.getStackInSlot(0);
        ItemStack oxygenBottle = inventory.getStackInSlot(1);

        if (sulfurDioxide.isEmpty() || oxygenBottle.isEmpty()) return false;

        // ⚠️「ModItems.SULFUR_DIOXIDE.get()」等は実際のID名に合わせてください
        if (!sulfurDioxide.is(ModItems.PURIFIED_SULFUR_DIOXIDE_DUST.get()) || !oxygenBottle.is(ModItems.OXYGEN_BOTTLE.get())) {
            return false;
        }

        // 出力枠（三酸化硫黄）と返却枠（空ビン）の受け入れシミュレーション
        ItemStack sulfurTrioxideOutput = new ItemStack(ModItems.SULFUR_TRIOXIDE_DUST.get());
        ItemStack glassBottleOutput = new ItemStack(Items.GLASS_BOTTLE);

        ItemStack remTrioxide = inventory.insertItem(2, sulfurTrioxideOutput, true);
        ItemStack remBottle = inventory.insertItem(3, glassBottleOutput, true);

        // 両方とも完全に収まるスペースがあればOK
        return remTrioxide.isEmpty() && remBottle.isEmpty();
    }

    private void completeOxidization() {
        // 原料を1個ずつ消費
        inventory.getStackInSlot(0).shrink(1);
        inventory.getStackInSlot(1).shrink(1);

        // 製品（三酸化硫黄）をスロット2に、空ビンをスロット3に搬出
        inventory.insertItem(2, new ItemStack(ModItems.SULFUR_TRIOXIDE_DUST.get()), false);
        inventory.insertItem(3, new ItemStack(Items.GLASS_BOTTLE), false);

        this.progress = 0;
        setChanged();
    }

    // --- NBT保存・読み込み処理 ---
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("Inventory", inventory.serializeNBT());
        pTag.putInt("Progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inventory.deserializeNBT(pTag.getCompound("Inventory"));
        progress = pTag.getInt("Progress");
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast();
        return super.getCapability(cap, side);
    }

    // --- Container用の実装（省略・基本セット） ---
    @Override public int getContainerSize() { return inventory.getSlots(); }
    @Override public boolean isEmpty() { return false; }
    @Override public ItemStack getItem(int index) { return inventory.getStackInSlot(index); }
    @Override public ItemStack removeItem(int index, int count) { return inventory.extractItem(index, count, false); }
    @Override public ItemStack removeItemNoUpdate(int index) { return ItemStack.EMPTY; }
    @Override public void setItem(int index, ItemStack stack) { inventory.setStackInSlot(index, stack); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() {}
    @Override public Component getDisplayName() { return Component.translatable(this.getBlockState().getBlock().getDescriptionId()); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new OxidizerMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}