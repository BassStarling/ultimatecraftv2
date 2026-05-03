package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.BatteryChargerBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class BatteryChargerBlock extends BaseEntityBlock {
    public BatteryChargerBlock(Properties properties) {
        super(properties);
    }

    // --- 右クリックでGUIを開く処理 ---
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BatteryChargerBlockEntity chargerEntity) {
                // NetworkHooksを使用してメニュー（GUI）を開く
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) entity, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // --- BlockEntityの紐付け ---
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryChargerBlockEntity(pos, state);
    }

    // --- 毎tickの処理（BlockEntityのtickを呼び出す） ---
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.BATTERY_CHARGER.get(), BatteryChargerBlockEntity::tick);
    }

    // --- ブロックが壊れた時に中身のアイテムをぶちまける ---
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BatteryChargerBlockEntity chargerEntity) {
                // ここでインベントリ内のアイテムをドロップさせる処理を呼ぶ
                // 実際の実装では ItemStackHandler からアイテムを取り出して spawnAtLocation します
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    // --- レンダリング設定（BaseEntityBlockを使う場合は必須） ---
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}