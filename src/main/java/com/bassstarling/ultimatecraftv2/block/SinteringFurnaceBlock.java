package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.SinteringFurnaceBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class SinteringFurnaceBlock extends BaseEntityBlock {

    public SinteringFurnaceBlock(Properties pProperties) {
        super(pProperties);
        // 初期状態の向きを北に設定
        this.registerDefaultState(this.stateDefinition.any().setValue(DirectionalBlock.FACING, Direction.NORTH));
    }

    // 【最重要】右クリックした時にGUI画面（Menu）を開く処理
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof SinteringFurnaceBlockEntity furnace) {
                // Forgeの仕様に則り、サーバー側のプレイヤーに対してMenuを開くパケットを送信
                NetworkHooks.openScreen((ServerPlayer) pPlayer, furnace, pPos);
            } else {
                throw new IllegalStateException("Our NamedContainerProvider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    // ブロックが破壊されたときに、中に入っていたアイテムを周囲に散らばらせる処理（ホッパー等と同じ仕様）
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SinteringFurnaceBlockEntity furnace) {
                // コンテナの中身をすべてドロップ
                net.minecraft.world.Containers.dropContents(pLevel, pPos, furnace);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL; // JSONモデルを使用して描画
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SinteringFurnaceBlockEntity(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DirectionalBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        // プレイヤーの正面を向くように配置
        return this.defaultBlockState().setValue(DirectionalBlock.FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        // サーバー側でのみ毎Tickのロジック（SinteringFurnaceBlockEntity.tick()）を動かすヘルパー
        return createTickerHelper(pBlockEntityType, ModBlockEntities.SINTERING_FURNACE.get(),
                (level, pos, state, be) -> be.tick());
    }
}