package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CrusherBlockEntity extends BlockEntity {
    public CrusherBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.CRUSHER.get(), p_155229_, p_155230_);
    }
    public void tick() {
        if (level == null || level.isClientSide) return;

        if (level.getGameTime() % 20 == 0) {
            System.out.println("Crusher ticking at " + worldPosition);
        }

        AABB box = new AABB(
                worldPosition.getX() - 0.2,
                worldPosition.getY(),
                worldPosition.getZ() - 0.2,
                worldPosition.getX() + 1.2,
                worldPosition.getY() + 1.5,
                worldPosition.getZ() + 1.2
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity bauxite = null;
        ItemEntity spark = null;

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();

            if (stack.is(ModItems.RAW_BAUXITE.get())) {
                bauxite = item;
            } else if (stack.is(ModItems.SPARK_STONE.get())
                    && SparkStone.getTier(stack) == 1) {
                spark = item;
            }
        }

        if (bauxite != null && spark != null) {

            // 消費
            bauxite.getItem().shrink(1);
            spark.getItem().shrink(1);

            // 空なら消す
            if (bauxite.getItem().isEmpty()) bauxite.discard();
            if (spark.getItem().isEmpty()) spark.discard();

            ItemStack result =
                    new ItemStack(ModItems.COARSE_BAUXITE_POWDER.get());

    level.addFreshEntity(new ItemEntity(
            level,
            worldPosition.getX() + 0.5,
            worldPosition.getY() + 1.0,
            worldPosition.getZ() + 1.2,
            result
    ));

            level.playSound(
                    null,
                    worldPosition,
                    SoundEvents.GRAVEL_BREAK,
                    SoundSource.BLOCKS,
                    0.7F,
                    1.0F
            );
        }
    }
}
