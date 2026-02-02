package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ElectrolyzerBlockEntity extends BlockEntity {

    public ElectrolyzerBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.ELECTROLYZER_.get(), pPos, pState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectrolyzerBlockEntity be) {
        if (level.isClientSide) return;

        // 下が水
        if (!level.getBlockState(pos.below()).is(Blocks.WATER)) return;

        // 上1ブロックを明示指定
        AABB box = new AABB(
                pos.getX(),
                pos.getY() + 1,
                pos.getZ(),
                pos.getX() + 1,
                pos.getY() + 2,
                pos.getZ() + 1
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity spark = null;
        ItemEntity bottle = null;

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();

            if (stack.is(ModItems.SPARK_STONE.get())
                    && SparkStone.getTier(stack) == 3) {
                spark = item;
            }
            else if (stack.is(Items.GLASS_BOTTLE)) {
                bottle = item;
            }
        }

        if (spark == null || bottle == null) return;

        // 消費（Crusher と同じ方式）
        spark.getItem().shrink(1);
        bottle.getItem().shrink(1);

        if (spark.getItem().isEmpty()) spark.discard();
        if (bottle.getItem().isEmpty()) bottle.discard();

        // 出力
        level.addFreshEntity(new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 1.1,
                pos.getZ() + 0.5,
                new ItemStack(ModItems.OXYGEN_BOTTLE.get())
        ));
    }
}