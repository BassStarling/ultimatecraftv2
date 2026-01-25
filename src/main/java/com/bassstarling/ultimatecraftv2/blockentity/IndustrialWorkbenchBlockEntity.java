package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.menu.IndustrialWorkbenchMenu;
import com.bassstarling.ultimatecraftv2.recipe.IndustrialRecipe;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IndustrialWorkbenchBlockEntity extends BlockEntity implements MenuProvider {

    // 5x5(25) + 完成品(1) = 26スロット
    private final ItemStackHandler itemHandler = new ItemStackHandler(26) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            // ★重要: 結果スロット(25)の変更時にはレシピ判定を行わない（無限ループ防止）
            if (slot < 25) {
                updateRecipe();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // クライアント側やホッパーなどが、結果スロット(25)にアイテムをねじ込むのを防ぐ
            return slot < 25;
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public IndustrialWorkbenchBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.INDUSTRIAL_WORKBENCH.get(), p_155229_, p_155230_);
    }

    // ★重要: これが作業台の「核」となる処理です
    public void updateRecipe() {
        if (this.level == null || this.level.isClientSide) return;

        // 25スロット分のコンテナを作る（レシピ判定用）
        SimpleContainer container = new SimpleContainer(25);
        for (int i = 0; i < 25; i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }

        // レシピマネージャーから、登録した Type に一致するものを探す
        Optional<IndustrialRecipe> recipe = this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.INDUSTRIAL_CRAFTING_TYPE.get(), container, level);

        if (recipe.isPresent()) {
            // 見つかったらクラフト結果を取得してセット
            itemHandler.setStackInSlot(25, recipe.get().assemble(container, level.registryAccess()));
        } else {
            // レシピがなければ完成品スロットを空にする
            itemHandler.setStackInSlot(25, ItemStack.EMPTY);
        }
    }

    // --- データの保存・読み込み (必須) ---
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    // --- 外部連携 (ホッパー等対応) ---
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        // ブロック自体の翻訳キーをそのまま代入
        return Component.translatable(ModBlocks.INDUSTRIAL_WORKBENCH.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        // サーバー側で必要。第3引数はBlockEntity自身を渡す
        return new IndustrialWorkbenchMenu(id, inventory, this);
    }
}
