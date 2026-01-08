package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class WashingMachineBlockEntity extends BlockEntity {
    public WashingMachineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.WASHING_MACHINE.get(), p_155229_, p_155230_);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, WashingMachineBlockEntity be) {
        if (level.isClientSide) return;

        // 下が水かどうか
        if (!level.getBlockState(pos.below()).is(Blocks.WATER)) return;

        // 判定範囲（ブロックの上）
        AABB box = new AABB(
                pos.getX(),
                pos.getY() + 1,
                pos.getZ(),
                pos.getX() + 1,
                pos.getY() + 2,
                pos.getZ() + 1
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity roughPowder = null;

        for (ItemEntity item : items) {
            if (item.getItem().is(ModItems.COARSE_BAUXITE_POWDER.get())) {
                roughPowder = item;
                break;
            }
        }

        if (roughPowder == null) return;

        // 消費
        roughPowder.discard();

        // 出力
        ItemStack result = new ItemStack(ModItems.WASHED_BAUXITE_POWDER.get());

        level.addFreshEntity(new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() - 0.2,
                result
        ));

        // 演出
        level.playSound(
                null,
                pos,
                SoundEvents.BOTTLE_FILL,
                SoundSource.BLOCKS,
                0.6f,
                1.0f
        );
    }
}
