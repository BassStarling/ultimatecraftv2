package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import com.bassstarling.ultimatecraftv2.util.SparkStoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HeatGeneratorBlockEntity extends BlockEntity {
    public HeatGeneratorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.HEAT_GENERATOR.get(), p_155229_, p_155230_);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, HeatGeneratorBlockEntity be) {
        if (level.isClientSide) return;

        boolean generatedThisTick = false;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (be.isActiveFurnace(neighborState)) {
                be.emitSpark(level, pos, dir);
                generatedThisTick = true;
            }
        }
    }
    private boolean isActiveFurnace(BlockState state) {
        return state.getBlock() instanceof AbstractFurnaceBlock
                && state.getValue(AbstractFurnaceBlock.LIT);
    }
    private void emitSpark(Level level, BlockPos pos, Direction dir) {

        // 噴出口位置（ブロック表面）
        double x = pos.getX() + 0.5 + dir.getStepX() * 0.6;
        double y = pos.getY() + 0.5 + dir.getStepY() * 0.6;
        double z = pos.getZ() + 0.5 + dir.getStepZ() * 0.6;

        ItemStack spark = SparkStoneUtil.createTier1(); // tier1（粉）

        ItemEntity item = new ItemEntity(level, x, y, z, spark);

        // 初速
        item.setDeltaMovement(
                dir.getStepX() * 0.1,
                0.05,
                dir.getStepZ() * 0.1
        );

        level.addFreshEntity(item);
    }
    public static ItemStack createTier1() {
        ItemStack stack = new ItemStack(ModItems.SPARK_STONE.get());
        stack.getOrCreateTag().putInt("Tier", 1);
        return stack;
    }
}
