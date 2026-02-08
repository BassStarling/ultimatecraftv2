package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.item.SparkStone;
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
                // 入力スロットに 粗ニッケル を許可
                case 0 -> stack.is(Items.COAL)
                        || stack.is(ModItems.UNFIRED_ELECTRODE.get())
                        || stack.is(ModItems.RAW_NICKEL.get());
                case 1 -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0
                        || stack.is(ModItems.SPARK_STONE.get());
                case 2 -> true;
                case 3 -> stack.is(Items.BUCKET);
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
                    int time = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);

                    // スパークストーン T4 の特殊処理
                    if (time <= 0 && fuel.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(fuel) == 4) {
                        time = 200;
                    }

                    if (time > 0) {
                        this.burnTime = time;
                        this.totalBurnTime = this.burnTime;

                        // 燃料の「残りカス（バケツなど）」を取得しておく
                        ItemStack containerStack = fuel.getCraftingRemainingItem();

                        // スタックを1減らす
                        fuel.shrink(1);

                        // もし燃料スロットが空になり、かつ「残りカス（空バケツ）」があるなら、そのスロットにセットする
                        if (fuel.isEmpty() && !containerStack.isEmpty()) {
                            itemHandler.setStackInSlot(1, containerStack);
                        }
                        setChanged();
                    }
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

    private boolean canProcess() {
        ItemStack input = itemHandler.getStackInSlot(0);
        ItemStack output = itemHandler.getStackInSlot(2);
        ItemStack fuel = itemHandler.getStackInSlot(1); // 燃料スロット

        if (input.isEmpty()) return false;

        if (input.is(ModItems.RAW_NICKEL.get())) {
            // 燃焼中である、または、今から燃やせるスパークストーン T4 がスロットにある
            boolean hasHeatOrFuel = this.burnTime > 0 ||
                    (!fuel.isEmpty() && fuel.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(fuel) == 4);

            return hasHeatOrFuel && (output.isEmpty() || (output.is(ModItems.NICKEL_INGOT.get()) && output.getCount() < 64));
        }

        // 既存のコークス・電極の判定
        if (input.is(Items.COAL)) {
            return (output.isEmpty() || (output.is(ModItems.COKE.get()) && output.getCount() < 64)) &&
                    fluidTank.getSpace() >= 250;
        }

        if (input.is(ModItems.UNFIRED_ELECTRODE.get())) {
            return (output.isEmpty() || (output.is(ModItems.COKE_ELECTRODE.get()) && output.getCount() < 64));
        }

        return false;
    }

    private void processItem() {
        ItemStack input = itemHandler.getStackInSlot(0);

        // ニッケルの処理
        if (input.is(ModItems.RAW_NICKEL.get())) {
            itemHandler.extractItem(0, 1, false);
            itemHandler.insertItem(2, new ItemStack(ModItems.NICKEL_INGOT.get()), false);
        }
        // 既存の処理
        else if (input.is(Items.COAL)) {
            itemHandler.extractItem(0, 1, false);
            itemHandler.insertItem(2, new ItemStack(ModItems.COKE.get()), false);
            fluidTank.fill(new FluidStack(ModFluids.SOURCE_TAR.get(), 250), IFluidHandler.FluidAction.EXECUTE);
        }
        else if (input.is(ModItems.UNFIRED_ELECTRODE.get())) {
            itemHandler.extractItem(0, 1, false);
            itemHandler.insertItem(2, new ItemStack(ModItems.COKE_ELECTRODE.get()), false);
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
        return new CokeOvenMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public Container getItemHandlerAsSimpleContainer() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        return container;
    }

    public void writeMenuGuideData(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.worldPosition);
    }
}