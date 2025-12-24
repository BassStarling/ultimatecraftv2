package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SparkCompressorBlockEntity extends BlockEntity {

    private ItemStack stack = ItemStack.EMPTY;
    private int progress = 0;
    private static final int MAX_PROGRESS = 40; // 2秒

    public SparkCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPARK_COMPRESSOR.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (stack.isEmpty()) {
            progress = 0;
            return;
        }

        if (!(stack.getItem() instanceof SparkStone)) {
            progress = 0;
            return;
        }

        int tier = SparkStone.getTier(stack);

        if (stack.getCount() < 3) {
            progress = 0;
            return;
        }

        progress++;

        if (progress >= MAX_PROGRESS) {
            // 消費
            stack.shrink(3);

            // 生成
            ItemStack result = new ItemStack(ModItems.SPARK_STONE.get());
            SparkStone.setTier(result, tier + 1);

            if (stack.isEmpty()) {
                stack = result;
            } else {
                Containers.dropItemStack(
                        level,
                        worldPosition.getX(),
                        worldPosition.getY() + 1,
                        worldPosition.getZ(),
                        result
                );
            }

            progress = 0;
            setChanged();
        }
    }

    /* ===== インベントリ最小実装 ===== */

    public ItemStack getItem() {
        return stack;
    }

    public void setItem(ItemStack stack) {
        this.stack = stack;
        setChanged();
    }
}
