package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class WashingMachineBlockEntity extends BlockEntity {
    public WashingMachineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.WASHING_MACHINE.get(), p_155229_, p_155230_);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WashingMachineBlockEntity be) {
        if (level.isClientSide) return;

        // 下が水かどうか
        if (!level.getBlockState(pos.below()).is(Blocks.WATER)) return;

        // 判定範囲
        AABB box = new AABB(
                pos.getX(), pos.getY() + 1, pos.getZ(),
                pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        for (ItemEntity itemEntity : items) {
            ItemStack inputStack = itemEntity.getItem();

            // ★ポイント1: 変数の型は 'ModItems' ではなく 'Item' にします
            Item resultItem = null;

            // ★ポイント2: .is() の中身は ModItems.NAME.get() で取得します
            if (inputStack.is(ModItems.COARSE_BAUXITE_POWDER.get())) {
                resultItem = ModItems.WASHED_BAUXITE_POWDER.get();
            }
            else if (inputStack.is(ModItems.ALUMINA.get())) {
                resultItem = ModItems.HIGH_PURITY_ALUMINA.get();
            }

            // マッチするレシピがあれば処理
            if (resultItem != null) {
                inputStack.shrink(1);

                if (inputStack.isEmpty()) {
                    itemEntity.discard();
                }

                // 結果をスポーン
                level.addFreshEntity(new ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() - 0.2,
                        new ItemStack(resultItem) // ここも Item 型を受け取ります
                ));

                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.6f, 1.0f);
                break;
            }
        }
    }
}