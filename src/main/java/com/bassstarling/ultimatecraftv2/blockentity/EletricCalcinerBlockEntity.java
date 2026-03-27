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

        // 判定範囲（ブロックの直上 0.5マスの高さ）
        AABB box = new AABB(
                pos.getX(), pos.getY() + 1, pos.getZ(),
                pos.getX() + 1, pos.getY() + 1.5, pos.getZ() + 1
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity sparkStoneEntity = null;
        ItemEntity inputEntity = null;
        ItemStack resultStack = ItemStack.EMPTY;

        // 1. スパークストーンの判定 (Tier 4 以上が必要な高熱処理と想定)
        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            if (stack.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(stack) >= 4) {
                sparkStoneEntity = item;
                break;
            }
        }

        if (sparkStoneEntity == null) return;

        // 2. 入力アイテムの判定
        for (ItemEntity item : items) {
            if (item == sparkStoneEntity) continue;

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
            // パターンC: 耐火粘土 → 耐火煉瓦
            else if (stack.is(ModItems.FIRE_CLAY_BALL.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.FIREBRICK.get());
                break;
            }
            // ★新規追加★ パターンD: 水酸化アルミニウム(白色綿毛状固体) → アルミナ
            else if (stack.is(ModItems.WHITE_FLUFFY_SOLID_OF_ALUMINIUM_HYDROXIDE.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.ALUMINA.get());
                break;
            }
        }

        // 3. 処理実行
        if (inputEntity != null && !resultStack.isEmpty()) {
            // 消費処理
            sparkStoneEntity.getItem().shrink(1);
            inputEntity.getItem().shrink(1);

            if (sparkStoneEntity.getItem().isEmpty()) sparkStoneEntity.discard();
            if (inputEntity.getItem().isEmpty()) inputEntity.discard();

            // 成果物のドロップ
            level.addFreshEntity(new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 1.1,
                    pos.getZ() + 0.5,
                    resultStack
            ));

            // 演出
            level.playSound(null, pos, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.8F, 1.0F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.05);
                // 精錬中っぽい煙も追加
                serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 2, 0.05, 0.05, 0.05, 0.02);
            }
        }
    }
}
