package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.block.SparkCompressorBlock;
import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparkCompressorBlockEntity extends BlockEntity {

    private boolean isAnimating = false;
    private int animationTicks = 0;

    public SparkCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPARK_COMPRESSOR.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        tickAnimation();

        BlockPos pos = worldPosition;

        AABB box = new AABB(pos)
                .inflate(0.4, 1.2, 0.4)
                .move(0, 1.0, 0);

        List<ItemEntity> items =
                level.getEntitiesOfClass(ItemEntity.class, box);

        // ★ 追加①：鉄板加工を先に判定
        if (tryMakeIronPlate(items)) {
            startAnimation();
            return;
        }

        // ★ 既存：スパーク圧縮
        tryCompressSparkStone(items);
    }

    private boolean tryMakeIronPlate(List<ItemEntity> items) {
        ItemEntity iron = null;
        ItemEntity spark = null;

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();

            if (stack.is(Items.IRON_INGOT)) {
                iron = item;
            } else if (stack.getItem() == ModItems.SPARK_STONE.get()
                    && SparkStone.getTier(stack) == 2) {
                spark = item;
            }
        }

        if (iron == null || spark == null) return false;

        // 消費
        iron.discard();
        spark.discard();

        // 生成
        ItemStack result = new ItemStack(ModItems.IRON_PLATE.get());
        level.addFreshEntity(new ItemEntity(
                level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 1.1,
                worldPosition.getZ() + 0.5,
                result
        ));

        level.playSound(
                null,
                worldPosition,
                SoundEvents.PISTON_EXTEND,
                SoundSource.BLOCKS,
                0.8F,
                0.9F
        );

        return true;
    }

    private void tryCompressSparkStone(List<ItemEntity> items) {
        Map<Integer, List<ItemEntity>> byTier = new HashMap<>();

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            if (stack.getItem() != ModItems.SPARK_STONE.get()) continue;

            int tier = SparkStone.getTier(stack);
            byTier.computeIfAbsent(tier, t -> new ArrayList<>()).add(item);
        }

        for (var entry : byTier.entrySet()) {
            int tier = entry.getKey();
            if (tier >= 7) continue;

            if (entry.getValue().size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    entry.getValue().get(i).discard();
                }

                ItemStack result = SparkStone.createWithTier(tier + 1);
                level.addFreshEntity(new ItemEntity(
                        level,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 1.1,
                        worldPosition.getZ() + 0.5,
                        result
                ));

                level.playSound(
                        null,
                        worldPosition,
                        SoundEvents.PISTON_EXTEND,
                        SoundSource.BLOCKS,
                        0.8F,
                        1.0F
                );

                startAnimation();
                break;
            }
        }
    }

    private void startAnimation() {
        if (isAnimating) return;

        isAnimating = true;
        animationTicks = 0;

        level.setBlock(
                worldPosition,
                getBlockState().setValue(SparkCompressorBlock.COMPRESSING, true),
                3
        );
    }

    private void tickAnimation() {
        if (!isAnimating) return;

        animationTicks++;
        if (animationTicks >= 10) {
            isAnimating = false;
            animationTicks = 0;

            level.setBlock(
                    worldPosition,
                    getBlockState().setValue(SparkCompressorBlock.COMPRESSING, false),
                    3
            );
        }
    }
}