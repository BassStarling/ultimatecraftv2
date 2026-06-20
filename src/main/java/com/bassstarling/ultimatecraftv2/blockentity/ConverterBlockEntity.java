package com.bassstarling.ultimatecraftv2.blockentity;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ConverterBlockEntity extends BlockEntity {

    public ConverterBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.CONVERTER_BE.get(), pPos, pState);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // ブロックの周囲（上部1.5ブロック分）のアイテムエンティティを検知する範囲
        AABB box = new AABB(
                worldPosition.getX() - 0.2, worldPosition.getY(), worldPosition.getZ() - 0.2,
                worldPosition.getX() + 1.2, worldPosition.getY() + 1.5, worldPosition.getZ() + 1.2
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity spark = null;    // スパークストーン (Tier 3)
        ItemEntity oxygen = null;   // 酸素入り瓶
        ItemEntity targetIron = null; // 投入された鉄素材（通常の銑鉄 または 脱硫処理焼結銑鉄）

        for (ItemEntity item : items) {
            if (!item.isAlive()) continue;
            ItemStack stack = item.getItem();

            if (stack.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(stack) == 3) {
                spark = item;
            } else if (stack.is(ModItems.OXYGEN_BOTTLE.get())) {
                oxygen = item;
            } else if (stack.is(ModItems.PIG_IRON.get()) || stack.is(ModItems.DESULFURIZED_SINTERED_PIG_IRON.get())) {
                // 通常の銑鉄、または脱硫処理焼結銑鉄のどちらでも検知・保持する
                targetIron = item;
            }

            // 3つの要素（スパーク、酸素、対象の鉄）がすべて揃ったらループを抜ける
            if (spark != null && oxygen != null && targetIron != null) break;
        }

        // 💡 3つの素材すべてが揃っている場合のみ、吹錬（加工）を実行
        if (spark != null && oxygen != null && targetIron != null) {

            // 生成されるアイテムのスタック（基本は鋼鉄）
            ItemStack resultStack = new ItemStack(ModItems.STEEL_INGOT.get());

            // 各素材を1つずつ安全に消費
            oxygen.getItem().shrink(1);
            targetIron.getItem().shrink(1);
            spark.getItem().shrink(1);

            // エンティティのクリーンアップ（スタック数が0になったアイテム実体をワールドから消去）
            if (oxygen.getItem().isEmpty()) oxygen.discard();
            if (targetIron.getItem().isEmpty()) targetIron.discard();
            if (spark.getItem().isEmpty()) spark.discard();

            // 結果アイテム（鋼鉄）と、副産物の「空のガラス瓶」を同じ位置にドロップ
            double dropX = worldPosition.getX() + 0.5;
            double dropY = worldPosition.getY() + 1.1;
            double dropZ = worldPosition.getZ() + 0.5;

            level.addFreshEntity(new ItemEntity(level, dropX, dropY, dropZ, resultStack));
            level.addFreshEntity(new ItemEntity(level, dropX, dropY, dropZ, new ItemStack(Items.GLASS_BOTTLE)));

            // 吹錬プロセスの激しいサウンドエフェクト
            level.playSound(null, worldPosition, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 0.7F, 0.8F);
        }
    }
}