package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ElectrolyzerBlockEntity extends BlockEntity {

    public ElectrolyzerBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.ELECTROLYZER_.get(), pPos, pState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectrolyzerBlockEntity be) {
        if (level.isClientSide) return;

        if (!level.getBlockState(pos.below()).is(Blocks.WATER)) return;

        AABB box = new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity sparkEntity = null;
        ItemEntity inputEntity = null;
        ItemStack resultStack = ItemStack.EMPTY;

        // 先にスパークストーンを探す（ティア判定をレシピ判定で使うため）
        for (ItemEntity item : items) {
            if (item.getItem().is(ModItems.SPARK_STONE.get())) {
                sparkEntity = item;
                break;
            }
        }

        if (sparkEntity == null) return;
        int tier = SparkStone.getTier(sparkEntity.getItem());

        // 入力アイテムの判定
        for (ItemEntity item : items) {
            if (item == sparkEntity) continue; // スパークストーン自身は飛ばす

            ItemStack stack = item.getItem();

            // パターンA: 空の瓶 + T3スパーク = 酸素ボトル
            if (stack.is(Items.GLASS_BOTTLE) && tier == 3) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.OXYGEN_BOTTLE.get());
                break;
            }
            // パターンB: 塩水入り瓶 + T4スパーク = 水酸化ナトリウム溶液
            else if (stack.is(ModItems.BRINE_BOTTLE.get()) && tier == 4) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.SODIUM_HYDROXIDE_SOLUTION_BOTTLE.get());
                break;
            }
            // パターンC: 泡状アルミナ + T4スパーク = 多孔質断熱ブロック
            else if (stack.is(ModItems.FOAMED_ALUMINA.get().asItem()) && tier == 4) {
                inputEntity = item;
                resultStack = new ItemStack(ModBlocks.POROUS_INSULATION_BLOCK.get().asItem());
                break;
            }
        }

        if (inputEntity == null || resultStack.isEmpty()) return;

        // 消費処理
        sparkEntity.getItem().shrink(1);
        inputEntity.getItem().shrink(1);

        if (sparkEntity.getItem().isEmpty()) sparkEntity.discard();
        if (inputEntity.getItem().isEmpty()) inputEntity.discard();

        // 出力
        level.addFreshEntity(new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 1.1,
                pos.getZ() + 0.5,
                resultStack
        ));

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
        }
    }
}