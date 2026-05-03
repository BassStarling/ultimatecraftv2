package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NaphthaCauldronBlockEntity extends BlockEntity {
    public NaphthaCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NAPHTHA_CAULDRON.get(), pos, state);
    }

    public void craftRubber(Level level, BlockPos pos) {
        // 化学反応の演出
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    20, 0.1, 0.1, 0.1, 0.05);
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.2f);
        }

        // 未加硫ゴムをドロップ
        Containers.dropItemStack(level, pos.getX(), pos.getY() + 1.0, pos.getZ(),
                new ItemStack(ModBlocks.RAW_RUBBER_BLOCK.get()));

        // 大釜を元に戻す
        level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
    }
}