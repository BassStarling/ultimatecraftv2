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
        ItemEntity ironEntity = null;
        ItemEntity sparkEntity = null;

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            if (stack.isEmpty()) continue;

            if (stack.is(Items.IRON_INGOT)) {
                ironEntity = item;
            } else if (stack.getItem() == ModItems.SPARK_STONE.get()
                    && SparkStone.getTier(stack) == 2) {
                sparkEntity = item;
            }
        }

        if (ironEntity == null || sparkEntity == null) return false;

        // ★ 修正：スタックから1つずつ消費する
        consumeOneItem(ironEntity);
        consumeOneItem(sparkEntity);

        // 生成
        ItemStack result = new ItemStack(ModItems.IRON_PLATE.get());
        level.addFreshEntity(new ItemEntity(
                level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 1.1,
                worldPosition.getZ() + 0.5,
                result
        ));

        level.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.8F, 0.9F);
        startAnimation(); // アニメーション開始（処理自体は止まらない）

        return true;
    }

    // 共通の消費メソッド
    private void consumeOneItem(ItemEntity entity) {
        ItemStack stack = entity.getItem();
        stack.shrink(1);
        if (stack.isEmpty()) {
            entity.discard();
        } else {
            // 重要：中身が減ったことをEntityに再セットして同期させる
            entity.setItem(stack);
        }
    }

    private void tryCompressSparkStone(List<ItemEntity> items) {
        // ティアごとの合計個数と、そのエンティティのリストを保持
        Map<Integer, Integer> totalCountByTier = new HashMap<>();
        Map<Integer, List<ItemEntity>> entitiesByTier = new HashMap<>();

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            if (stack.isEmpty() || stack.getItem() != ModItems.SPARK_STONE.get()) continue;

            int tier = SparkStone.getTier(stack);
            totalCountByTier.put(tier, totalCountByTier.getOrDefault(tier, 0) + stack.getCount());
            entitiesByTier.computeIfAbsent(tier, t -> new ArrayList<>()).add(item);
        }

        for (var entry : totalCountByTier.entrySet()) {
            int tier = entry.getKey();
            int count = entry.getValue();

            // ティア7未満かつ、合計3個以上あれば圧縮
            if (tier < 7 && count >= 3) {
                // ★ 修正：合計3個分を消費するロジック
                int toConsume = 3;
                List<ItemEntity> targets = entitiesByTier.get(tier);

                for (ItemEntity target : targets) {
                    if (toConsume <= 0) break;

                    ItemStack stack = target.getItem();
                    int currentStackSize = stack.getCount();

                    if (currentStackSize <= toConsume) {
                        // このエンティティを全部使い切る場合
                        toConsume -= currentStackSize;
                        target.discard();
                    } else {
                        // このエンティティの一部だけ使う場合
                        stack.shrink(toConsume);
                        target.setItem(stack); // 見た目とデータを更新
                        toConsume = 0;
                    }
                }

                // 次のティアの石を生成
                ItemStack result = SparkStone.createWithTier(tier + 1);
                level.addFreshEntity(new ItemEntity(
                        level,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 1.1,
                        worldPosition.getZ() + 0.5,
                        result
                ));

                level.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.8F, 1.0F);
                startAnimation();
                break; // 1回のtickで1回のみ圧縮（高速化したい場合はここを調整）
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