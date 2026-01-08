package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EletricCalcinerBlockEntity extends BlockEntity {

    public EletricCalcinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELETRIC_CALCINER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EletricCalcinerBlockEntity be) {
        if (level.isClientSide) return;

        AABB box = new AABB(
                pos.getX(),
                pos.getY() + 1,
                pos.getZ(),
                pos.getX() + 1,
                pos.getY() + 1.5,
                pos.getZ() + 1
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity bauxite = null;
        ItemEntity sparkStone = null;

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();

            if (stack.is(ModItems.WASHED_BAUXITE_POWDER.get())) {
                bauxite = item;
            }

            if (stack.is(ModItems.SPARK_STONE.get())
                    && SparkStone.getTier(stack) == 4) {
                sparkStone = item;
            }
        }

        if (bauxite != null && sparkStone != null) {
            bauxite.discard();
            sparkStone.discard();

            ItemStack result = new ItemStack(ModItems.ALUMINA.get());

            level.addFreshEntity(new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 1.1,
                    pos.getZ() + 0.5,
                    result
            ));

            level.playSound(
                    null,
                    pos,
                    SoundEvents.BLASTFURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    0.8F,
                    1.0F
            );
        }
    }
}
