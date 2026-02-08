package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.block.DisposableArcFurnaceBlock;
import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DisposableArcFurnaceBlockEntity extends BlockEntity {

    private int scanCooldown = 0;

    public DisposableArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISPOSABLE_ARC__FURNACE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        boolean isSpent = state.getValue(DisposableArcFurnaceBlock.SPENT);

        // 判定範囲（ブロックの真上）
        AABB box = new AABB(worldPosition).move(0, 1.0, 0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        if (!isSpent) {
            // 【新品状態】グラファイト電極の製造
            tryCreateGraphite(items);
        } else {
            // 【使用済み状態】コーク電極による修理
            tryRepair(items);
        }
    }

    private void tryCreateGraphite(List<ItemEntity> items) {
        ItemEntity spark = findItem(items, ModItems.SPARK_STONE.get(), 7); // Tier 7
        ItemEntity cokeElectrode = findItem(items, ModItems.COKE_ELECTRODE.get(), -1);

        if (spark != null && cokeElectrode != null) {
            // 消費（1スタック全部消えないように注意）
            consumeOne(spark);
            consumeOne(cokeElectrode);

            // グラファイト電極を生成
            spawnResult(new ItemStack(ModItems.GRAPHITE_ELECTRODE.get()));

            // ブロックを使用済み状態に更新
            level.setBlock(worldPosition, getBlockState().setValue(DisposableArcFurnaceBlock.SPENT, true), 3);

            // 激しい演出
            level.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5F, 2.0F);
        }
    }

    private void tryRepair(List<ItemEntity> items) {
        ItemEntity cokeElectrode = findItem(items, ModItems.COKE_ELECTRODE.get(), -1);

        if (cokeElectrode != null) {
            consumeOne(cokeElectrode);

            // ブロックを新品状態に戻す
            level.setBlock(worldPosition, getBlockState().setValue(DisposableArcFurnaceBlock.SPENT, false), 3);
        }
    }

    private ItemEntity findItem(List<ItemEntity> items, Item targetItem, int targetTier) {
        for (ItemEntity entity : items) {
            ItemStack stack = entity.getItem();
            if (stack.is(targetItem)) {
                if (targetTier == -1 || SparkStone.getTier(stack) == targetTier) {
                    return entity;
                }
            }
        }
        return null;
    }

    private void consumeOne(ItemEntity entity) {
        ItemStack stack = entity.getItem();
        stack.shrink(1);
        if (stack.isEmpty()) {
            entity.discard();
        } else {
            entity.setItem(stack);
        }
    }

    private void spawnResult(ItemStack stack) {
        if (level == null) return;

        // 出現位置：ブロックの中心の少し上
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.1; // ブロックの上面より少し高い位置
        double z = worldPosition.getZ() + 0.5;

        ItemEntity entity = new ItemEntity(level, x, y, z, stack);

        // 演出：少しだけ上に「ポンッ」と跳ねさせる（お好みで）
        entity.setDeltaMovement(0, 0.1, 0);

        // 世界にエンティティを登録
        level.addFreshEntity(entity);
    }
}
