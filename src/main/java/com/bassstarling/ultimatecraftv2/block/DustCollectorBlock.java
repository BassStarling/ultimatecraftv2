package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.DustCollectorBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

import javax.annotation.Nullable;

public class DustCollectorBlock extends BaseEntityBlock {
    public DustCollectorBlock(Properties pProperties) {
        super(pProperties);
        // 初期状態の向きを北に設定
        this.registerDefaultState(this.stateDefinition.any().setValue(DirectionalBlock.FACING, Direction.NORTH));
    }

    // 【修正1】BaseEntityBlock を継承する場合、これがないとブロックが「透明（不可視）」になります
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL; // 通常のJSONモデルを使って描画する指定
    }

    // 【追加】右クリックで画面を開かずにアイテムを直接出し入れするロジック
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof DustCollectorBlockEntity collector) {
                ItemStack held = pPlayer.getItemInHand(pHand);

                if (!held.isEmpty()) {
                    // 手持ちのアイテムを入力スロット(0)に入れようと試みる
                    ItemStack remainder = collector.inventory.insertItem(0, held.copy(), false);
                    pPlayer.setItemInHand(pHand, remainder);
                    collector.setChanged();
                    return InteractionResult.SUCCESS;
                } else {
                    // 手ぶらで右クリックしたら、出力スロット(1, 2)に溜まっているアイテムをプレイヤーに渡す
                    for (int i = 1; i <= 2; i++) {
                        ItemStack out = collector.inventory.getStackInSlot(i);
                        if (!out.isEmpty()) {
                            pPlayer.addItem(out.copy());
                            collector.inventory.setStackInSlot(i, ItemStack.EMPTY);
                            collector.setChanged();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DustCollectorBlockEntity(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DirectionalBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        // プレイヤーの向いている方向の逆（正面）を向くように配置
        return this.defaultBlockState().setValue(DirectionalBlock.FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    // 【修正2】getTickerの比較対象を、ModBlockEntitiesで登録した正しいTypeに固定します
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        // あなたのModBlockEntitiesに登録されているであろう集塵機のEntityTypeと一致するかチェック
        return createTickerHelper(pBlockEntityType, ModBlockEntities.DUST_COLLECTOR.get(),
                (level, pos, state, be) -> be.tick());
    }
}