package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.Container;

public class SparkGeneratorBlockEntity extends BlockEntity {

    private int progress = 0;
    private static final int MAX_PROGRESS = 20;

    public SparkGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPARK_GENERATOR.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;
        if (level.isClientSide) return;

        if (canGenerateSpark()) {
            progress++;

            if (progress >= MAX_PROGRESS) {
                generateSpark();
                onSparkGenerated();
                progress = 0;
                setChanged();
            }
        } else {
            progress = 0;
        }
    }

    private boolean canGenerateSpark() {
        return this.level.hasNeighborSignal(this.worldPosition);
    }

    private void generateSpark() {
        ItemStack stack = new ItemStack(ModItems.SPARK_STONE.get());
        // 生成するtierは適宜
        SparkStone.setTier(stack, 0);

        Containers.dropItemStack(
                this.level,
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 1,
                this.worldPosition.getZ() + 0.5,
                stack
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

        if (level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.1;
            double z = worldPosition.getZ() + 0.5;

            clientLevel.addParticle(
                    ParticleTypes.WAX_OFF,
                    x + (level.random.nextDouble() - 0.5) * 0.3,
                    y,
                    z + (level.random.nextDouble() - 0.5) * 0.3,
                    0.0, 0.0, 0.0
            );
        }
    }
}
