package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.CokeOvenBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CokeOvenBlock extends BaseEntityBlock {
    public CokeOvenBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // 右クリック時の処理
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CokeOvenBlockEntity cokeOven) {
                // 第3引数のラムダ式の中で、BlockEntityのメソッドを明示的に呼び出す！
                NetworkHooks.openScreen((ServerPlayer) player, cokeOven, buf -> {
                    cokeOven.writeMenuGuideData(buf); // ここで「使用」されるようになります
                });
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // BlockEntityの生成
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CokeOvenBlockEntity(pos, state);
    }

    // 毎チックの更新処理（Ticker）の登録
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // levelがサーバー側の場合のみtickを動かす
        return createTickerHelper(type, ModBlockEntities.COKE_OVEN.get(),
                (level1, pos, state1, blockEntity) -> blockEntity.tick(level1, pos, state1));
    }

    // ブロックが破壊されたときに中身をぶちまける処理
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CokeOvenBlockEntity cokeOven) {
                // アイテムをドロップさせる
                Containers.dropContents(level, pos, cokeOven.getItemHandlerAsSimpleContainer());
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        // これを MODEL にしないと、ブロックモデルのJSONを読み込んでくれません
        return RenderShape.MODEL;
    }
}
