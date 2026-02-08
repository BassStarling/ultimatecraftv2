package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.datagen.ModItemTagProvider;
import com.bassstarling.ultimatecraftv2.menu.CastingMachineMenu;
import com.bassstarling.ultimatecraftv2.menu.ElectrolyticFurnaceMenu;
import com.bassstarling.ultimatecraftv2.recipe.CastingRecipe;
import com.bassstarling.ultimatecraftv2.recipe.ModRecipes;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.bassstarling.ultimatecraftv2.registry.ModItems.MOLD_TAG;

public class CastingMachineBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged(); // 中身が変わったら保存フラグを立てる
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> true; // 材料スロットは何でもOK
                case 1 -> stack.is(MOLD_TAG); // 型スロットはタグ付きのみ！
                case 2 -> false; // 出力スロットは搬入不可
                default -> false;
            };
        }
    };
    private int progress = 0;
    private final int maxProgress = 200; // 10秒(20tick * 10)

    public CastingMachineBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.CASTING_MACHINE_BE.get(), pPos, pState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CastingMachineBlockEntity be) {
        if (level.isClientSide) return;

        // --- 1. アイテムの吸引処理 (5tickに1回) ---
        if (level.getGameTime() % 5 == 0) {
            // ブロックの上面（高さ16px）の少し上を検知
            AABB suctionArea = new AABB(pos).move(0, 0.9D, 0).expandTowards(0, 0.2D, 0);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, suctionArea);

            for (ItemEntity itemEntity : items) {
                if (!itemEntity.isAlive()) continue;

                ItemStack stack = itemEntity.getItem();

                // スロット0（上）またはスロット1（下）に入れられるか試行
                // 既にアイテムが入っている場合は、同じアイテムかつスタックに余裕がある場合のみ入る
                ItemStack reminder = stack.copy();

                // まず上のスロット(0)を試す
                reminder = be.itemHandler.insertItem(0, reminder, false);

                // 余りがあれば下のスロット(1)を試す
                if (!reminder.isEmpty()) {
                    reminder = be.itemHandler.insertItem(1, reminder, false);
                }

                // アイテムのEntityを更新または削除
                if (reminder.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(reminder);
                }
            }
        }

        Optional<CastingRecipe> recipe = getRecipe(be);

        if (recipe.isPresent()) {
            be.progress++;
            if (be.progress >= be.maxProgress) {
                craftItem(be, recipe.get());
                be.progress = 0;
            }
            setChanged(level, pos, state);
        } else {
            be.progress = 0;
        }
    }

    private static Optional<CastingRecipe> getRecipe(CastingMachineBlockEntity be) {
        SimpleContainer inventory = new SimpleContainer(be.itemHandler.getSlots());
        for (int i = 0; i < be.itemHandler.getSlots(); i++) {
            inventory.setItem(i, be.itemHandler.getStackInSlot(i));
        }
        return be.getLevel().getRecipeManager().getRecipeFor(ModRecipes.CASTING_TYPE.get(), inventory, be.getLevel());
    }

    private static void craftItem(CastingMachineBlockEntity be, CastingRecipe recipe) {
        // 1. 材料（スロット0）だけ1つ消費する
        be.itemHandler.extractItem(0, 1, false);

        // 2. スロット1（型）は消費しない（何もしない）

        // 3. 完成品（スロット2）へセット
        ItemStack result = recipe.assemble(null, be.getLevel().registryAccess());
        ItemStack currentInOutput = be.itemHandler.getStackInSlot(2);

        if (currentInOutput.isEmpty()) {
            be.itemHandler.setStackInSlot(2, result.copy());
        } else {
            currentInOutput.grow(result.getCount());
        }

        be.setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.ultimatecraftv2.casting_machine");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CastingMachineMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? CastingMachineBlockEntity.this.progress : CastingMachineBlockEntity.this.maxProgress;
        }
        @Override
        public void set(int index, int value) {
            if (index == 0) CastingMachineBlockEntity.this.progress = value;
        }
        @Override
        public int getCount() { return 2; }
    };

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    // インベントリ機能のキャッシュ（動作を軽くするため）
    private final LazyOptional<IItemHandler> optional = LazyOptional.of(() -> this.itemHandler);

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 「アイテムハンドラー機能ある？」と聞かれたら「あるよ」と答える
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return optional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        optional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("casting_machine.progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("casting_machine.progress");
    }
}
