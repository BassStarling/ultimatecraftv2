package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.ThrowerBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ThrowerBlock extends BaseEntityBlock {

    public ThrowerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ThrowerBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        // モデルを表示するために必要
        return RenderShape.MODEL;
    }

    // 毎チックの更新（tickメソッド）を有効にするための設定
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        // クライアント側では動かさず、サーバー側のみでロジックを実行
        return createTickerHelper(pBlockEntityType, ModBlockEntities.THROWER_BE.get(),
                (lvl, pos, state, be) -> be.tick());
    }
}