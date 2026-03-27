package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.menu.DigesterMenu;
import com.bassstarling.ultimatecraftv2.recipe.DigestingRecipe;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DigesterBlockEntity extends BlockEntity implements MenuProvider {

    private final FluidTank inputTank = new FluidTank(10000);
    // タンク1: 苛性ソーダ (入力) / タンク2: アルミ酸ナトリウム (出力)
    private final FluidTank outputTank = new FluidTank(10000);

    // 0: 粉(Slot3), 1: 苛性ソーダ瓶(Slot1), 2: 空瓶(Slot2)
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                // Slot 0: 粉（レシピの材料アイテムならOK）
                case 0 -> isRecipeIngredient(stack);

                // Slot 1: 液体瓶(下のisBottleIngredientに加筆しなければならない)
                case 1 -> isBottleIngredient(stack);

                // Slot 2: 出力用（プレイヤーによる搬入は禁止）
                case 2 -> stack.is(Items.GLASS_BOTTLE);

                default -> super.isItemValid(slot, stack);
            };
        }
    };

    /**
     * そのアイテム（瓶）が、いずれかの浸出レシピの「入力液体」を持っているか判定
     */

    private boolean isBottleIngredient(ItemStack stack) {
        // レシピ判定を無視して、特定のアイテムなら許可するように強制
        return stack.is(ModItems.SODIUM_HYDROXIDE_SOLUTION_BOTTLE.get());
    }

    /**
     * アイテムから液体を取り出すヘルパー（Modの仕様に合わせて実装）
     */
    private FluidStack getFluidFromBottle(ItemStack stack) {

        // return FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);

        if (stack.is(ModItems.SODIUM_HYDROXIDE_SOLUTION_BOTTLE.get())) {
            return new FluidStack(ModFluids.SOURCE_SODIUM_HYDROXIDE.get(), 250);
        }
        return FluidStack.EMPTY;
    }

    /**
     * そのアイテムがいずれかの浸出レシピの材料に含まれているか判定するヘルパー
     */
    private boolean isRecipeIngredient(ItemStack stack) {
        if (level == null || stack.isEmpty()) return false;

        return level.getRecipeManager().getAllRecipesFor(DigestingRecipe.Type.INSTANCE)
                .stream()
                .anyMatch(recipe -> recipe.getInputItem().getItem() == stack.getItem());
    }

    private int progress = 0;
    private  int maxProgress = 100;

    public DigesterBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.DIGESTER_BE.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        processBottles();
        exportFluid();

        // 1. 材料スロット(Slot 0)をチェック
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        inventory.setItem(0, itemHandler.getStackInSlot(0));

        Optional<DigestingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(DigestingRecipe.Type.INSTANCE, inventory, level);

        if (recipe.isPresent()) {
            DigestingRecipe r = recipe.get();
            this.maxProgress = r.getCookTime();

            // ★最重要チェック：液体が足りているか「毎チック」確認
            if (canProcess(r)) {
                // progressを進める。ここではまだ液体も粉も消費しない。
                progress++;

                if (progress >= r.getCookTime()) {
                    // ★完了した瞬間だけ「液体」と「アイテム」を同時に消費
                    inputTank.drain(r.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    itemHandler.extractItem(0, 1, false);

                    // 出力タンクへ成果物を入れる
                    outputTank.fill(r.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);

                    progress = 0; // 完了したのでリセット
                }
            } else {
                // 途中で液体が空になった、または出力タンクがいっぱいになったら即座に中断
                progress = 0;
            }
        } else {
            // レシピに合うアイテムがないならリセット
            progress = 0;
        }
        setChanged();
    }

    private boolean canProcess(DigestingRecipe recipe) {
        if (inputTank.isEmpty()) return false;

        // 修正：種類の一致判定をより確実な方法に変更
        boolean isCorrectType = inputTank.getFluid().isFluidEqual(recipe.getInputFluid());

        // 量の判定
        boolean hasEnoughAmount = inputTank.getFluidAmount() >= recipe.getInputFluid().getAmount();

        // 出力先の空き判定
        boolean canFillOutput = outputTank.fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.SIMULATE) == recipe.getOutputFluid().getAmount();

        return isCorrectType && hasEnoughAmount && canFillOutput;
    }

    private int debugTimer = 0; // ログのスパム防止用

    private void processBottles() {
        ItemStack bottleStack = itemHandler.getStackInSlot(1);
        if (bottleStack.isEmpty()) return; // 瓶が入っていない時は何もしない（ログも出さない）

        debugTimer++;
        boolean shouldLog = (debugTimer % 40 == 0); // 2秒に1回だけログを出力

        FluidStack fluidInBottle = getFluidFromBottle(bottleStack);
        if (fluidInBottle.isEmpty()) {
            if (shouldLog) System.out.println("DEBUG 1: 瓶から液体を取得できません！ アイテムが一致していない可能性があります。 現在のアイテム: " + bottleStack.getItem());
            return;
        }

        // 1. タンクに液体の空きがあるか確認
        int filled = inputTank.fill(fluidInBottle, IFluidHandler.FluidAction.SIMULATE);
        if (filled != fluidInBottle.getAmount()) {
            if (shouldLog) {
                String currentFluid = inputTank.isEmpty() ? "空" : inputTank.getFluid().getDisplayName().getString();
                System.out.println("DEBUG 2: タンクに液体が入りません！ 容量不足、または別の液体が入っています。 タンクの中身: " + inputTank.getFluidAmount() + "mB (" + currentFluid + ")");
            }
            return;
        }

        // 2. Slot 2 に「ガラス瓶 1つ」が入る余裕があるかシミュレート
        ItemStack reminder = itemHandler.insertItem(2, new ItemStack(Items.GLASS_BOTTLE), true);
        if (!reminder.isEmpty()) {
            ItemStack stuckItem = itemHandler.getStackInSlot(2);
            if (shouldLog) System.out.println("DEBUG 3: 空き瓶スロットがいっぱいです！ 正体 -> [" + stuckItem.getItem() + "] 個数: " + stuckItem.getCount());
            return;
        }

        if (shouldLog) System.out.println("DEBUG 4: 全条件クリア！ 液体を搬入します。");

        // 3. 全ての条件をクリアしたので実行
        inputTank.fill(fluidInBottle, IFluidHandler.FluidAction.EXECUTE);
        itemHandler.extractItem(1, 1, false); // 溶液瓶を1つ消費
        itemHandler.insertItem(2, new ItemStack(Items.GLASS_BOTTLE), false); // ガラス瓶を1つ追加
        setChanged();
    }

    private void exportFluid() {
        if (outputTank.isEmpty()) return;

        for (Direction direction : Direction.values()) {
            // ★ 修正：上下方向（UP, DOWN）ならスキップする
            if (direction.getAxis().isVertical()) continue;

            BlockPos neighborPos = worldPosition.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.is(ModBlocks.FILTER.get())) {
                BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                if (neighborBE != null) {
                    neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                        FluidStack stackToTransfer = outputTank.drain(250, IFluidHandler.FluidAction.SIMULATE);
                        if (!stackToTransfer.isEmpty()) {
                            int filled = handler.fill(stackToTransfer, IFluidHandler.FluidAction.EXECUTE);
                            if (filled > 0) {
                                outputTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                                setChanged();
                                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ultimatecraftv2.digester");
    }

    // --- Getter メソッド ---
    public FluidTank getInputTank() {
        return this.inputTank;
    }

    public FluidTank getOutputTank() {
        return this.outputTank;
    }

    // --- ContainerData (Menuと通信するためのデータ) ---
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> inputTank.getFluidAmount();
                case 3 -> outputTank.getFluidAmount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> {
                    // クライアント側のタンクに「水酸化ナトリウム」として量をセット
                    inputTank.setFluid(new FluidStack(ModFluids.SOURCE_SODIUM_HYDROXIDE.get(), value));
                }
                case 3 -> {
                    // クライアント側のタンクに「アルミ酸ナトリウム」として量をセット
                    outputTank.setFluid(new FluidStack(ModFluids.SOURCE_SODIUM_ALUMINATE_SUSPENSION.get(), value));
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("digester.progress", progress);
        nbt.put("InputTank", inputTank.writeToNBT(new CompoundTag()));
        nbt.put("OutputTank", outputTank.writeToNBT(new CompoundTag()));
        nbt.put("Inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt("digester.progress");
        inputTank.readFromNBT(nbt.getCompound("InputTank"));
        outputTank.readFromNBT(nbt.getCompound("OutputTank"));
        itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyInputTank = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyOutputTank = LazyOptional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyInputTank = LazyOptional.of(() -> inputTank);
        lazyOutputTank = LazyOptional.of(() -> outputTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyInputTank.invalidate();
        lazyOutputTank.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 1. アイテムハンドラーの処理
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        // 2. 流体（液体）ハンドラーの処理
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            // side == null（内部処理や一部のMod）の場合は入力タンクを返す
            if (side == null) return lazyInputTank.cast();

            // 縦（上下）からは搬出（outputTank）のみ
            if (side.getAxis().isVertical()) {
                return lazyOutputTank.cast();
            }

            // 横（前後左右）からは搬入（inputTank）のみ
            return lazyInputTank.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new DigesterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    private void craftItem() {
        Level level = this.level;
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        // 現在のアイテムと液体に合うレシピを探す
        Optional<DigestingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(DigestingRecipe.Type.INSTANCE, inventory, level);

        if (recipe.isPresent()) {
            DigestingRecipe r = recipe.get();
            // ここで r.getInputFluid() や r.getOutputFluid() を使って処理を行う
            // progress の最大値も r.getCookTime() に合わせる
        }
    }

    // クライアントがブロックを読み込んだ時にデータを送る
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt); // 現在のタンクやプログレスの状態をすべて書き込む
        return nbt;
    }

    // データのパケットを受け取った時
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    // サーバーでデータが変わった時にクライアントへ再描画を促す
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}