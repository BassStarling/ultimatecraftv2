package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.EthylenePlantBlockEntity;
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

public class EthylenePlantBlock extends BaseEntityBlock {

    public EthylenePlantBlock(Properties pProperties) {
        super(pProperties);
        // デフォルトの向きを北向きに初期化
        this.registerDefaultState(this.stateDefinition.any().setValue(DirectionalBlock.FACING, Direction.NORTH));
    }

    // 右クリック時にサーバー側でプレイヤーに対応するGUI（Menu）を開くパケットをトリガー
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof EthylenePlantBlockEntity plant) {
                // ForgeのNetworkHooksを利用してMenuを安全に同期起動
                NetworkHooks.openScreen((ServerPlayer) pPlayer, plant, pPos);
            } else {
                throw new IllegalStateException("Our NamedContainerProvider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    // ブロックが破壊された際、中の5スロット（ナフサ、水、燃料、エチレン、空バケツ）の中身を周囲に散らばらせる
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof EthylenePlantBlockEntity plant) {
                // コンテナ内の中身を全ドロップ
                net.minecraft.world.Containers.dropContents(pLevel, pPos, plant);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL; // JSONモデルファイル（.json）を参照して描画するバニラ標準スタイル
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EthylenePlantBlockEntity(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DirectionalBlock.FACING); // 4方向（または上下含む6方向）のデータ定義を追加
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        // プレイヤーが設置したとき、その正面を向くように逆方向のDirectionを設定
        return this.defaultBlockState().setValue(DirectionalBlock.FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        // クライアント側では動かさず、サーバー側でのみ毎Tickの熱分解クラッキングロジックを実行するヘルパー
        return createTickerHelper(pBlockEntityType, ModBlockEntities.ETHYLENE__PLANT.get(),
                (level, pos, state, be) -> be.tick());
    }
}