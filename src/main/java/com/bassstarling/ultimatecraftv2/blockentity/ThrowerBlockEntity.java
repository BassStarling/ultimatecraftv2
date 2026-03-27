package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ThrowerBlockEntity extends BlockEntity {
    // 動作間隔（例：20tick = 1秒に1回）
    private int cooldown = 20;

    public ThrowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THROWER_BE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (--cooldown <= 0) {
            if (tryThrowItem()) {
                cooldown = 20; // 成功したらクールダウンリセット
            } else {
                cooldown = 5;  // 失敗（投げる先がない、アイテムがない等）ならすぐ再試行
            }
        }
    }

    private boolean tryThrowItem() {
        // 1. 周囲のコンテナ（チェスト、樽、機械）を探す
        for (Direction side : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(side));
            if (neighbor == null) continue;

            // アイテムハンドラーを取得
            LazyOptional<IItemHandler> cap = neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite());
            if (cap.isPresent()) {
                IItemHandler handler = cap.orElseThrow(RuntimeException::new);

                // 2. 左上のスロット（基本はスロット0）からアイテムを1つ抽出試行
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        // 3. 投げ出す先の「空き」を探す
                        Direction targetDir = findLaunchDirection();
                        if (targetDir != null) {
                            // 実際にアイテムを1つ取り出す
                            ItemStack thrownStack = handler.extractItem(i, 1, false);
                            launchItem(targetDir, thrownStack);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 「ブロックがない方向」を探すロジック
    private Direction findLaunchDirection() {
        // 候補リスト
        List<Direction> candidates = new ArrayList<>();

        for (Direction side : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(side);

            // 1. 隣にブロックがあるかチェック
            // .isAir() は空気、洞窟の空気、構造物の空気をすべて含みます
            if (!level.getBlockState(neighborPos).isAir()) {

                // 2. その「反対側」をドロップ先候補にする
                Direction opposite = side.getOpposite();
                BlockPos targetPos = worldPosition.relative(opposite);

                // 3. 反対側が「空気」であれば候補に追加
                if (level.getBlockState(targetPos).isAir()) {
                    candidates.add(opposite);
                }
            }
        }

        // 候補がある場合は、最初に見つかった方向を返す
        if (!candidates.isEmpty()) {
            return candidates.get(0);
        }

        // 全方位が空気、または全方位が埋まっている場合のフォールバック
        for (Direction side : Direction.values()) {
            if (level.getBlockState(worldPosition.relative(side)).isAir()) {
                return side;
            }
        }

        return null;
    }

    private void launchItem(Direction dir, ItemStack stack) {
        double x = worldPosition.getX() + 0.5 + dir.getStepX() * 0.7;
        double y = worldPosition.getY() + 0.5 + dir.getStepY() * 0.7;
        double z = worldPosition.getZ() + 0.5 + dir.getStepZ() * 0.7;

        ItemEntity entity = new ItemEntity(level, x, y, z, stack);

        // 投げ出す速度ベクトル
        double speed = 0.2;
        entity.setDeltaMovement(
                dir.getStepX() * speed,
                dir.getStepY() * speed + 0.1, // 少し上にふんわり投げる
                dir.getStepZ() * speed
        );

        level.addFreshEntity(entity);
        level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 2.0F);
    }
}