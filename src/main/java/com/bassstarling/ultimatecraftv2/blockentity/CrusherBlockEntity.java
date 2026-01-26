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

        AABB box = new AABB(
                worldPosition.getX() - 0.2, worldPosition.getY(), worldPosition.getZ() - 0.2,
                worldPosition.getX() + 1.2, worldPosition.getY() + 1.5, worldPosition.getZ() + 1.2
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity targetInput = null; // 粉砕対象（ボーキサイト or コークス）
        ItemEntity spark = null;       // スパークストーン
        ItemStack resultStack = ItemStack.EMPTY; // 生成されるアイテムのスタック

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();

            // 1. スパークストーンの判定（共通）
            if (stack.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(stack) == 1) {
                spark = item;
            }
            // 2. 原料の判定：ボーキサイト
            else if (stack.is(ModItems.RAW_BAUXITE.get())) {
                targetInput = item;
                resultStack = new ItemStack(ModItems.COARSE_BAUXITE_POWDER.get());
            }
            // 3. 原料の判定：コークス（今回の追加分）
            else if (stack.is(ModItems.COKE.get())) {
                targetInput = item;
                resultStack = new ItemStack(ModItems.COKE_DUST.get());
            }
        }

        // 両方の素材が揃っていて、かつ結果が定義されている場合のみ実行
        if (targetInput != null && spark != null && !resultStack.isEmpty()) {

            // 素材を消費
            targetInput.getItem().shrink(1);
            spark.getItem().shrink(1);

            // エンティティのクリーンアップ
            if (targetInput.getItem().isEmpty()) targetInput.discard();
            if (spark.getItem().isEmpty()) spark.discard();

            // 結果アイテムを放出
            level.addFreshEntity(new ItemEntity(
                    level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.1, // 少し高くして重なりを防ぐ
                    worldPosition.getZ() + 0.5, // 中央付近に落とす
                    resultStack
            ));

            // エフェクト
            level.playSound(null, worldPosition, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 0.7F, 0.8F);
        }
    }
}
