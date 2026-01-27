package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.menu.CokeOvenMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class CokeOvenBlockEntity extends BlockEntity implements MenuProvider {
    // 1. スロット定義（0:入力, 1:出力, 2:バケツ用）
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == 3) return 1; // バケツスロットの最大数を1個に固定
            return super.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(Items.COAL)|| stack.is(ModItems.UNFIRED_GRAPHITE_ELECTRODE.get());
                case 1 -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0; // 燃料
                case 2 -> true; // 出力スロットには入れられない
                case 3 -> stack.is(Items.BUCKET); // バケツスロット
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    // 2. 液体タンク定義（2000mB = バケツ2杯分）
    private final FluidTank fluidTank = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() { setChanged(); }
    };
    private int burnTime;
    private int totalBurnTime;
    private int progress = 0;
    private final int maxProgress = 200; // 20秒

    public CokeOvenBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COKE_OVEN.get(), pPos, pBlockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean isBurning = this.burnTime > 0;
        if (isBurning) this.burnTime--;

        if (canProcess()) {
            // 燃料補給ロジック
            if (this.burnTime <= 0) {
                ItemStack fuel = itemHandler.getStackInSlot(1);
                if (!fuel.isEmpty()) {
                    this.burnTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
                    this.totalBurnTime = this.burnTime;
                    fuel.shrink(1);
                    setChanged();
                }
            }

            // 加工作業
            if (this.burnTime > 0) {
                progress++;
                if (progress >= maxProgress) {
                    processItem();
                    progress = 0;
                }
            }
        } else {
            progress = 0;
        }

        // バケツの置き換えロジック (スロット3)
        handleBucketConversion();
    }

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> fluidTank.getFluidAmount();
                case 3 -> fluidTank.getCapacity();
                case 4 -> burnTime;       // ← これ
                case 5 -> totalBurnTime;  // ← これ
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            switch (index) {
            case 0 -> progress = value;
            case 1 -> {}
            case 2 -> fluidTank.setFluid(new FluidStack(ModFluids.SOURCE_TAR.get(), value));
            case 3 -> {}
            case 4 -> burnTime = value;
            case 5 -> totalBurnTime = value;
        }}
        @Override
        public int getCount() {
            return 6; }
    };

    public ContainerData getData() {
        return this.data;
    }

// CokeOvenBlockEntity.java

    private boolean canProcess() {
        ItemStack input = itemHandler.getStackInSlot(0);
        ItemStack output = itemHandler.getStackInSlot(2);

        if (input.isEmpty()) return false;

        // A: 石炭の場合
        if (input.is(Items.COAL)) {
            return (output.isEmpty() || (output.is(ModItems.COKE.get()) && output.getCount() < 64)) &&
                    fluidTank.getSpace() >= 250;
        }

        // B: 未焼成電極の場合（液体は出ない・消費もしない設定）
        if (input.is(ModItems.UNFIRED_GRAPHITE_ELECTRODE.get())) {
            return (output.isEmpty() || (output.is(ModItems.BAKED_CARBON_ELECTRODE.get()) && output.getCount() < 64));
        }

        return false;
    }

    private void processItem() {
        ItemStack input = itemHandler.getStackInSlot(0);

        if (input.is(Items.COAL)) {
            // 石炭の処理
            itemHandler.extractItem(0, 1, false);
            itemHandler.insertItem(2, new ItemStack(ModItems.COKE.get()), false);
            fluidTank.fill(new FluidStack(ModFluids.SOURCE_TAR.get(), 250), IFluidHandler.FluidAction.EXECUTE);
        }
        else if (input.is(ModItems.UNFIRED_GRAPHITE_ELECTRODE.get())) {
            // 電極の処理（ベーク）
            itemHandler.extractItem(0, 1, false);
            itemHandler.insertItem(2, new ItemStack(ModItems.BAKED_CARBON_ELECTRODE.get()), false);
            // 電極を焼くときはタールは出ない（むしろタールが固まる工程なので）
        }
    }

    private void handleBucketConversion() {
        ItemStack bucketSlot = itemHandler.getStackInSlot(3);
        // 空バケツが入っており、かつタンクに1000mB以上ある場合、タールバケツに「置き換える」
        if (bucketSlot.is(Items.BUCKET) && fluidTank.getFluidAmount() >= 1000) {
            itemHandler.setStackInSlot(3, new ItemStack(ModItems.TAR_BUCKET.get()));
            fluidTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("progress", progress);
        fluidTank.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("progress");
        fluidTank.readFromNBT(nbt);
    }

    public IItemHandler getItemHandler() { return itemHandler; }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.COKEOVEN.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        // サーバー側でMenuをインスタンス化
        return new CokeOvenMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    // CokeOvenBlockEntity.java 内
    public Container getItemHandlerAsSimpleContainer() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        return container;
    }

    // ネットワーク経由でBlockPosをクライアントに送る（MenuType登録時のdata.readBlockPos用）
    // これを忘れるとクライアント側でBEが見つからずクラッシュします
    // これが NetworkHooks.openScreen の第3引数で呼ばれます
    public void writeMenuGuideData(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.worldPosition);
    }

    // NBT保存, Capability等の定型文は省略
}