package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.LithiumIonBatteryItem;
import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.item.UVLightItem;
import com.bassstarling.ultimatecraftv2.menu.BatteryChargerMenu;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class BatteryChargerBlockEntity extends BlockEntity implements MenuProvider {
    // --- フィールドの定義 ---
    private int energyBuffer = 0;
    public static final int MAX_ENERGY = 36000;

    // コンストラクタ
    public BatteryChargerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATTERY_CHARGER.get(), pos, state);
    }

    // --- セーブ（NBT書き込み） ---
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("EnergyBuffer", this.energyBuffer);
        nbt.put("Inventory", inventory.serializeNBT());
        super.saveAdditional(nbt);
    }

    // --- ロード（NBT読み込み） ---
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.energyBuffer = nbt.getInt("EnergyBuffer");
        inventory.deserializeNBT(nbt.getCompound("Inventory"));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BatteryChargerBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack stack = entity.inventory.getStackInSlot(0);
        if (stack.isEmpty()) return;

        // 1. スパークストーン補給
        if (stack.getItem() instanceof SparkStone) {
            int tier = SparkStone.getTier(stack);
            int energyToAdd = switch (tier) {
                case 1 -> 20; case 2 -> 40; case 3 -> 80;
                case 4 -> 160; case 5 -> 320; case 6 -> 640; case 7 -> 1280;
                default -> 10;
            };

            if (entity.energyBuffer + energyToAdd <= MAX_ENERGY) {
                entity.energyBuffer += energyToAdd;
                stack.shrink(1);
                entity.setChanged();
            }
        }
        // 2. アイテム充電
        else if (entity.energyBuffer > 0 && stack.isDamageableItem()) {
            if (stack.getItem() instanceof LithiumIonBatteryItem || stack.getItem() instanceof UVLightItem) {
                if (stack.getDamageValue() > 0) {
                    stack.setDamageValue(stack.getDamageValue() - 1);
                    entity.energyBuffer -= 1;

                    // % 20 の制限を外し、常に setChanged を呼ぶか、
                    // もしくはデータの同期を強制するために頻度を上げます。
                    entity.setChanged();
                }
            }
        }
    }

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return BatteryChargerBlockEntity.this.energyBuffer / 10; // 10分の1にして送る
        }
        @Override
        public void set(int index, int value) {
            BatteryChargerBlockEntity.this.energyBuffer = value * 10;
        }
        @Override
        public int getCount() { return 1; }
    };

    @Override
    public Component getDisplayName() {
        return Component.literal("Battery Charger");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new BatteryChargerMenu(id, inv, this, this.data);
    }

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> inventory);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public ContainerData getContainerData() {
        return this.data;
    }
}