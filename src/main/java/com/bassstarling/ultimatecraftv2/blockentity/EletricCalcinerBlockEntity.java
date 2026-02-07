package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EletricCalcinerBlockEntity extends BlockEntity {

    public EletricCalcinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELETRIC_CALCINER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EletricCalcinerBlockEntity be) {
        if (level.isClientSide) return;

        // 判定範囲
        AABB box = new AABB(
                pos.getX(), pos.getY() + 1, pos.getZ(),
                pos.getX() + 1, pos.getY() + 1.5, pos.getZ() + 1
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity sparkStone = null;
        ItemEntity inputEntity = null;
        ItemStack resultStack = ItemStack.EMPTY;

        // 1. まずスパークストーン(T4)を探す
        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            if (stack.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(stack) == 4) {
                sparkStone = item;
                break;
            }
        }

        // スパークストーンがないなら終了
        if (sparkStone == null) return;

        // 2. 他のアイテム（入力物）を判定
        for (ItemEntity item : items) {
            if (item == sparkStone) continue; // スパークストーン自身は除外

            ItemStack stack = item.getItem();

            // パターンA: 洗浄済みボーキサイト粉末 → アルミナ
            if (stack.is(ModItems.WASHED_BAUXITE_POWDER.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.ALUMINA.get());
                break;
            }
            // パターンB: 泡状アルミナ → 多孔質断熱ブロック
            else if (stack.is(ModItems.FOAMED_ALUMINA.get().asItem())) {
                inputEntity = item;
                resultStack = new ItemStack(ModBlocks.POROUS_INSULATION_BLOCK.get().asItem());
                break;
            }
        }

        // 入力物が見つかれば処理実行
        if (inputEntity != null && !resultStack.isEmpty()) {
            // 消費（1つずつ減らす）
            sparkStone.getItem().shrink(1);
            inputEntity.getItem().shrink(1);

            if (sparkStone.getItem().isEmpty()) sparkStone.discard();
            if (inputEntity.getItem().isEmpty()) inputEntity.discard();

            // 出力生成
            level.addFreshEntity(new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 1.1,
                    pos.getZ() + 0.5,
                    resultStack
            ));

            // 共通の効果音
            level.playSound(null, pos, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.8F, 1.0F);

            // サーバー側で演出パーティクル（任意）
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.02);
            }
        }
    }
}
