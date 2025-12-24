package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.EnumMap;

public class SparkGeneratorBlockEntity extends BlockEntity {

    private int progress = 0;
    private static final int MAX_PROGRESS = 20;
    private final EnumMap<Direction, Boolean> lastPowered = new EnumMap<>(Direction.class);

    public SparkGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPARK_GENERATOR.get(), pos, state);

        for (Direction dir : Direction.values()) {
            lastPowered.put(dir, false);
        }
    }

    public boolean hasRedstoneSignal() {
        return level != null && level.hasNeighborSignal(worldPosition);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SparkGeneratorBlockEntity be) {
        if (level.isClientSide) return;

        for (Direction dir : Direction.values()) {
            boolean poweredNow = level.getSignal(pos.relative(dir), dir) > 0;
            boolean poweredBefore = be.lastPowered.get(dir);

            // 立ち上がり検出（面ごと）
            if (poweredNow && !poweredBefore) {
                be.generateSpark(dir);
            }

            be.lastPowered.put(dir, poweredNow);
        }
    }

    private boolean canGenerateSpark() {
        return this.level.hasNeighborSignal(this.worldPosition);
    }

    private boolean wasPowered = false;

    private void generateSpark(Direction dir) {
        if (level == null) return;

        ItemStack spark = new ItemStack(ModItems.SPARK_STONE.get());
        spark.getOrCreateTag().putInt(SparkStone.TIER_KEY, 1);

        Containers.dropItemStack(
                level,
                worldPosition.getX() + 0.5 + dir.getStepX() * 0.6,
                worldPosition.getY() + 0.5 + dir.getStepY() * 0.6,
                worldPosition.getZ() + 0.5 + dir.getStepZ() * 0.6,
                spark
        );
    }

    private void onSparkGenerated() {
        if (!level.isClientSide) {
            level.playSound(
                    null,
                    worldPosition,
                    SoundEvents.STONE_BREAK,
                    SoundSource.BLOCKS,
                    0.7f,
                    1.3f
            );
        }
    }
}
