package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.bassstarling.ultimatecraftv2.registry.ModBlockEntities.*;

public class SparkGeneratorBlockEntity extends BlockEntity {

    private final SimpleContainer inventory = new SimpleContainer(1);
    private final boolean[] lastPowered = new boolean[6];
    private int progress = 0;
    private static final int MAX_PROGRESS = 20;

    public SparkGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPARK_GENERATOR.get(), pos, state);
    }
    // ===== tick処理 =====
    public static void tick(Level level, BlockPos pos, BlockState state, SparkGeneratorBlockEntity be) {
        if (level == null || level.isClientSide) return;

        if (canGenerateSpark()) {
            progress++;

            if(progress >= MAX_PROGRESS) {
                generateSpark();
                onSparkGenerated();
                progress = 0;
                setChanged();
            }
        } else {
            progress = 0;
        }

        for (Direction dir : Direction.values()) {
            int idx = dir.ordinal();
            boolean poweredNow = level.getSignal(pos.relative(dir), dir) > 0;
            boolean poweredBefore = be.lastPowered[idx];

            // 0 → 1 の立ち上がり
            if (!poweredBefore && poweredNow) {
                be.generateSpark();
            }

            be.lastPowered[idx] = poweredNow;
        }
    }

    private void generateSpark() {
        ItemStack spark = new ItemStack(ModItems.SPARK_STONE.get());
        SparkStone.setTier(spark, 0); // 粉

        Containers.dropItemStack(
                level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 1.0,
                worldPosition.getZ() + 0.5,
                spark
        );
    }
    private boolean canGenerateSpark() {
        // 例：レッドストーン信号があるとき
        return level.hasNeighborSignal(worldPosition);
    }

    // ===== NBT保存 =====
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.createTag());
        for (int i = 0; i < 6; i++) {
            tag.putBoolean("LastPowered" + i, lastPowered[i]);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.fromTag(tag.getList("Inventory", 10));
        for (int i = 0; i < 6; i++) {
            lastPowered[i] = tag.getBoolean("LastPowered" + i);
        }
    }
    private void onSparkGenerated() {

        // サウンド（サーバー）
        if (!level.isClientSide) {
            level.playSound(
                    null,
                    worldPosition,
                    SoundEvents.STONE_BREAK,
                    SoundSource.BLOCKS,
                    0.8f,
                    1.4f
            );
        }

        // パーティクル（クライアント）
        if (level.isClientSide && level instanceof ClientLevel clientLevel) {

            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.1;
            double z = worldPosition.getZ() + 0.5;

            for (int i = 0; i < 6; i++) {
                clientLevel.addParticle(
                        ParticleTypes.WAX_OFF,
                        x + (level.random.nextDouble() - 0.5) * 0.4,
                        y,
                        z + (level.random.nextDouble() - 0.5) * 0.4,
                        0.0,
                        0.05,
                        0.0
                );
            }
        }
    }
}
