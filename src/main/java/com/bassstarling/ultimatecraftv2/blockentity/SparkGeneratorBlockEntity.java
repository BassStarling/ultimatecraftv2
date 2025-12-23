package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.bassstarling.ultimatecraftv2.registry.ModBlockEntities.*;

public class SparkGeneratorBlockEntity extends BlockEntity {

    private final SimpleContainer inventory = new SimpleContainer(1);
    private final boolean[] lastPowered = new boolean[6];

    public SparkGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPARK_GENERATOR.get(), pos, state);
    }
    // ===== tick処理 =====
    public static void tick(Level level, BlockPos pos, BlockState state, SparkGeneratorBlockEntity be) {
        if (level.isClientSide) return;

        for (Direction dir : Direction.values()) {
            int idx = dir.ordinal();
            boolean poweredNow = level.getSignal(pos.relative(dir), dir) > 0;
            boolean poweredBefore = be.lastPowered[idx];

            // 0 → 1 の立ち上がり
            if (!poweredBefore && poweredNow) {
                be.generateSpark();
            }

            be.lastPowered[idx] = poweredNow;
        }
    }

    private void generateSpark() {
        ItemStack stack = inventory.getItem(0);

        if (stack.isEmpty()) {
            inventory.setItem(0, new ItemStack(ModItems.SPARK_DUST.get(), 1));
            setChanged();
        } else if (stack.is(ModItems.SPARK_DUST.get()) && stack.getCount() < stack.getMaxStackSize()) {
            stack.grow(1);
            setChanged();
        }
    }

    // ===== NBT保存 =====
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.createTag());
        for (int i = 0; i < 6; i++) {
            tag.putBoolean("LastPowered" + i, lastPowered[i]);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.fromTag(tag.getList("Inventory", 10));
        for (int i = 0; i < 6; i++) {
            lastPowered[i] = tag.getBoolean("LastPowered" + i);
        }
    }
}
