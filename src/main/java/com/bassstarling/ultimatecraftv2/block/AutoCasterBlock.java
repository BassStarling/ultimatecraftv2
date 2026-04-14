package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.AutoCasterBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class AutoCasterBlock extends BaseEntityBlock {
    public AutoCasterBlock(Properties pProperties) {
        super(pProperties);
    }

    // 右クリックでチャットにステータスを表示
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (be instanceof AutoCasterBlockEntity caster) {
                int energy = caster.getEnergyStorage().getEnergyStored();
                int maxEnergy = caster.getEnergyStorage().getMaxEnergyStored();
                ItemStack stack = caster.getItemHandler().getStackInSlot(0);

                String itemInfo = stack.isEmpty() ? "空" : stack.getHoverName().getString() + " x" + stack.getCount();

                pPlayer.sendSystemMessage(Component.literal("§b[自動鋳造機ステータス]"));
                pPlayer.sendSystemMessage(Component.literal("エネルギー: " + energy + " / " + maxEnergy + " FE"));
                pPlayer.sendSystemMessage(Component.literal("内部在庫: " + itemInfo));
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AutoCasterBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.AUTO_CASTER_BE.get(),
                (lvl, pos, state, be) -> ((AutoCasterBlockEntity)be).tick());
    }
}