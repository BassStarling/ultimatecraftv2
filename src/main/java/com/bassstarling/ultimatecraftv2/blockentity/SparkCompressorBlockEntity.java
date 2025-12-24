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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparkCompressorBlockEntity extends BlockEntity {
    public SparkCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPARK_COMPRESSOR.get(), pos, state);
    }
    public void tick() {
        if (level == null || level.isClientSide) return;


        AABB box = new AABB(
                worldPosition.getX(),
                worldPosition.getY() + 1,
                worldPosition.getZ(),
                worldPosition.getX() + 1,
                worldPosition.getY() + 2,
                worldPosition.getZ() + 1
        );

        List<ItemEntity> items =
                level.getEntitiesOfClass(ItemEntity.class, box);

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
                // 消費
                for (int i = 0; i < 3; i++) {
                    entry.getValue().get(i).discard();
                }

                // 生成
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
                level.setBlock(
                        worldPosition,
                        getBlockState().setValue(SparkCompressorBlock.COMPRESSING, true),
                        3
                );


                break; // 1tick1圧縮
            }
            if (getBlockState().getValue(SparkCompressorBlock.COMPRESSING)) {
                animationTicks++;
                if (animationTicks >= 10) {
                    level.setBlock(
                            worldPosition,
                            getBlockState().setValue(SparkCompressorBlock.COMPRESSING, false),
                            3
                    );
                    animationTicks = 0;
                }
            }

        }
    }
    private int animationTicks = 0;
}
