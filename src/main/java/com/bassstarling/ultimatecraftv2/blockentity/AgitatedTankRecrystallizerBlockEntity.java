package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.menu.AgitatedTankRecrystallizerMenu;
import com.bassstarling.ultimatecraftv2.recipe.ModRecipes;
import com.bassstarling.ultimatecraftv2.recipe.RecrystallizingRecipe;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AgitatedTankRecrystallizerBlockEntity extends BlockEntity implements MenuProvider, Container {
    // ストレージ定義
    public final FluidTank inputTank = new FluidTank(10000);
    public final FluidTank outputTank = new FluidTank(10000);
    public final ItemStackHandler inventory = new ItemStackHandler(2); // 0:種, 1:出力
    public final EnergyStorage sparkTank = new EnergyStorage(36000);

    // プレイヤー操作変数
    private int userInputFlow = 0;
    private int userInputPower = 0;

    // 内部状態変数
    private int progress = 0;
    private int maxProgress = 0;
    private float currentTemperature = 20.0f;
    private int coolingEfficiency = 0;

    // キャッシュ用
    private RecrystallizingRecipe currentRecipe = null;

    public AgitatedTankRecrystallizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AGITATEDTANK_RECRYSTALLIZER.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // スパーク吸収
        if (sparkTank.getEnergyStored() <= (sparkTank.getMaxEnergyStored() - 1280)) {
            pickupAndAbsorbItems();
        }

        validateRecipe();

        if (currentRecipe != null && canProcess()) {
            processTick();
        } else {
            resetProgress();
        }
    }

    private void validateRecipe() {
        // 入力タンクまたは種結晶スロットが空ならレシピ破棄
        if (inputTank.isEmpty() || inventory.getStackInSlot(0).isEmpty()) {
            currentRecipe = null;
            return;
        }

        // 現在のレシピが有効か、または新しいレシピを探す
        if (currentRecipe == null || !currentRecipe.matches(new SimpleContainer(inventory.getStackInSlot(0)), level)
                || !inputTank.getFluid().isFluidEqual(currentRecipe.getInputFluid())) {

            currentRecipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.RECRYSTALLIZING_TYPE.get(), new SimpleContainer(inventory.getStackInSlot(0)), level)
                    .filter(r -> inputTank.getFluidAmount() >= r.getInputFluid().getAmount()
                            && inputTank.getFluid().isFluidEqual(r.getInputFluid()))
                    .orElse(null);

            if (currentRecipe != null) {
                this.maxProgress = currentRecipe.getProcessTime();
            }
        }
    }

    private void processTick() {
        // スパーク消費
        if (currentRecipe.needsSpark()) {
            if (sparkTank.getEnergyStored() < 10) return; // 停止
            sparkTank.extractEnergy(10, false);
        }

        // 理想値との比較ロジック
        float progressPercent = (float) this.progress / this.maxProgress;

        // 冷却効率 (理想流水量との比較)
        float coolingFactor = 1.0f;
        if (currentRecipe.needsCooling()) {
            float flowDiff = Math.abs(currentRecipe.getIdealFlow() - userInputFlow);
            this.coolingEfficiency = (int) Math.max(0, 100 - flowDiff);
            coolingFactor = this.coolingEfficiency / 100.0f;
        }

        // 2. 動的な理想電力 (進行度によって低下する)
        float stirringFactor = 1.0f;
        if (currentRecipe.needsSpark()) {
            int baseIdealPower = currentRecipe.getIdealPower();
            int dynamicIdealPower = (progressPercent < 0.4f) ? baseIdealPower :
                    (int) (baseIdealPower * (1.0f - ((progressPercent - 0.4f) / 0.6f * 0.7f)));

            float powerDiff = Math.abs(dynamicIdealPower - userInputPower);
            // 理想から40以上離れるとマイナス（結晶破壊）
            stirringFactor = (powerDiff > 40) ? -0.5f : (1.0f - (powerDiff / 40.0f));
        }

        // 温度シミュレーション
        float targetT = currentRecipe.getTargetTemperature();
        // 冷却効率が良いほど目標温度へ、悪いほど室温(20度)へ引っ張られる
        float envTemp = 20.0f;
        float actualTarget = targetT * coolingFactor + envTemp * (1.0f - coolingFactor);
        this.currentTemperature += (actualTarget - currentTemperature) * 0.01f;

        // 進行度の加算
        this.progress += (int) (1 * coolingFactor * stirringFactor);
        if (this.progress < 0) this.progress = 0;

        // 完了判定
        if (this.progress >= maxProgress) {
            completeRecipe();
        }

        pickupAndAbsorbItems();
    }

    private void completeRecipe() {
        if (currentRecipe == null) return;

        ItemStack result = currentRecipe.getOutputItem();
        FluidStack fluidOut = currentRecipe.getOutputFluid();

        if (inventory.insertItem(1, result, true).isEmpty() &&
                outputTank.fill(fluidOut, IFluidHandler.FluidAction.SIMULATE) == fluidOut.getAmount()) {

            // 消費処理
            // 入力タンクからレシピ指定量を引く
            inputTank.drain(currentRecipe.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
            // 種結晶スロット(0)から1つ減らす
            inventory.getStackInSlot(0).shrink(1);

            // 生成処理
            // 出力スロット(1)にアイテムを実投入
            inventory.insertItem(1, result.copy(), false);
            // 出力タンクに流体を実注入
            outputTank.fill(fluidOut.copy(), IFluidHandler.FluidAction.EXECUTE);

            resetProgress();
            setChanged();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        // 完了時は currentRecipe を null にして次のマッチングへ
        this.currentRecipe = null;
    }

    private boolean canProcess() {
        if (currentRecipe == null) return false;

        ItemStack outputStack = currentRecipe.getOutputItem();
        // 出力スロット(1)にアイテムが入る余裕があるか、廃液タンクに空きがあるかを確認
        boolean canInsertItem = inventory.insertItem(1, outputStack, true).getCount() < outputStack.getCount() || inventory.getStackInSlot(1).isEmpty();
        boolean canFillFluid = outputTank.fill(currentRecipe.getOutputFluid(), IFluidHandler.FluidAction.SIMULATE) == currentRecipe.getOutputFluid().getAmount();

        return canInsertItem && canFillFluid;
    }

    private void pickupAndAbsorbItems() {
        // ブロックの周囲1マスの範囲を定義
        AABB area = new AABB(worldPosition).inflate(1.0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();

            // スパークストーンであるかチェック
            if (!stack.isEmpty() && stack.is(ModItems.SPARK_STONE.get())) {
                int tier = SparkStone.getTier(stack);

                // 指定されたTierごとの充填量
                int energyToAdd = switch (tier) {
                    case 0 -> 10;
                    case 1 -> 20;
                    case 2 -> 40;
                    case 3 -> 80;
                    case 4 -> 160;
                    case 5 -> 320;
                    case 6 -> 640;
                    case 7 -> 1280;
                    default -> 5;
                };

                // エネルギーを充填し、アイテムを1つ減らす
                sparkTank.receiveEnergy(energyToAdd, false);
                stack.shrink(1);

                // アイテムがなくなったら実体を消去
                if (stack.isEmpty()) {
                    itemEntity.discard();
                }

                setChanged();
                break; // 1チックに1つずつ吸収するように制限（負荷対策）
            }
        }
    }

    // GUI同期用 Setter/Getter
    public void setFlow(int flow) {
        this.userInputFlow = flow;
    }

    public void setPower(int power) {
        this.userInputPower = power;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new AgitatedTankRecrystallizerMenu(p_39954_, p_39955_, this, this.data);
    }

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> sparkTank.getEnergyStored();
                case 3 -> sparkTank.getMaxEnergyStored();
                case 4 -> (int) (currentTemperature * 100); // floatは100倍してintで送る
                case 5 -> coolingEfficiency;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 5 -> coolingEfficiency = value;
                // 他の読み取り専用データはセット不要
            }
        }

        @Override
        public int getCount() { return 6; }
    };

    public int getUserInputFlow() {
        return this.userInputFlow;
    }

    public int getUserInputPower() {
        return this.userInputPower;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // タンクの中身が変わったときに呼び出すための補助メソッド
    public void updateClient() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setUserInputFlow(int flow) {
        this.userInputFlow = flow;
        this.setChanged(); // データの変更をマークし、保存対象にする
    }

    public void setUserInputPower(int power) {
        this.userInputPower = power;
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        // 指定スロットから指定数取り出す
        ItemStack result = inventory.extractItem(index, count, false);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        // スロットの中身を空にして、古い中身を返す
        ItemStack stack = inventory.getStackInSlot(index);
        inventory.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        inventory.setStackInSlot(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        // プレイヤーが離れすぎていないかチェックする標準メソッド
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }
}