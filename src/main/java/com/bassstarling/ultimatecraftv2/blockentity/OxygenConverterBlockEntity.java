package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class OxygenConverterBlockEntity extends BlockEntity {

    public OxygenConverterBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.OXYGEN_CONVERTER_BE.get(), pPos, pState);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        AABB box = new AABB(
                worldPosition.getX() - 0.2, worldPosition.getY(), worldPosition.getZ() - 0.2,
                worldPosition.getX() + 1.2, worldPosition.getY() + 1.5, worldPosition.getZ() + 1.2
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemStack resultStack = ItemStack.EMPTY; // 生成されるアイテムのスタック
        ItemEntity spark = null;   // スパークストーン
        ItemEntity oxygen = null;  // 酸素入り瓶
        ItemEntity pigIron = null; // 銑鉄

        for (ItemEntity item : items) {
            if (!item.isAlive()) continue;
            ItemStack stack = item.getItem();

            if (stack.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(stack) == 3) {
                spark = item;
            } else if (stack.is(ModItems.OXYGEN_BOTTLE.get())) {
                oxygen = item;
            } else if (stack.is(ModItems.PIG_IRON.get())) {
                pigIron = item;
            }

            // 3つとも見つかったら、これ以上ループを回す必要はないので抜ける
            if (spark != null && oxygen != null && pigIron != null) break;
        }

        // 2. 「3つすべてが揃っている」場合のみ、以下の処理を実行する
        if (spark != null && oxygen != null && pigIron != null) {

            // 結果を「鋼鉄」に設定
            resultStack = new ItemStack(ModItems.STEEL_INGOT.get());

            // 各素材を1つずつ消費
            oxygen.getItem().shrink(1);
            pigIron.getItem().shrink(1);
            // スパークストーンも消費する場合（消費したくないなら下の1行を消す）
            spark.getItem().shrink(1);

            // エンティティのクリーンアップ（スタックが0になったら消す）
            if (oxygen.getItem().isEmpty()) oxygen.discard();
            if (pigIron.getItem().isEmpty()) pigIron.discard();
            if (spark.getItem().isEmpty()) spark.discard();

            // 結果アイテム（鋼鉄）と、副産物の「空の瓶」を放出
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5, resultStack));
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5, new ItemStack(Items.GLASS_BOTTLE)));

            // エフェクト
            level.playSound(null, worldPosition, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 0.7F, 0.8F);
        }
    }
}
