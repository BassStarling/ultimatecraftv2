package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.CastingMachineBlockEntity;
import com.bassstarling.ultimatecraftv2.blockentity.ElectrolyticFurnaceBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class CastingMachineBlock extends BaseEntityBlock {
    public CastingMachineBlock(Properties props) {
        super(props);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CastingMachineBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        return createTickerHelper(type, ModBlockEntities.CASTING_MACHINE_BE.get(),
                (lvl, pos, st, be) -> CastingMachineBlockEntity.tick(lvl, pos, st, be));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // --- パターン1: 「融解アルミニウム入りバケツ」を持っている場合の処理 ---
        if (heldItem.is(ModItems.MOLTEN_ALUMINIUM_IN_BUCKET.get())) {
            if (!level.isClientSide) {
                // 1. 手持ちのバケツを1つ消費
                heldItem.shrink(1);

                // 2. 「アルミニウムインゴット」を渡す
                // ItemHandlerHelperを使うと、インベントリがいっぱいの時に足元に落としてくれるので便利です
                ItemStack ingot = new ItemStack(ModItems.ALUMINIUM_INGOT.get());
                ItemHandlerHelper.giveItemToPlayer(player, ingot);

                // 3. 「空のバケツ」を渡す
                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                ItemHandlerHelper.giveItemToPlayer(player, emptyBucket);

                // (オプション) ジュッという音を鳴らす
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            // 処理が終わったらここで成功を返して終了（GUIは開かない）
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // --- パターン2: それ以外の場合（GUIを開く） ---
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CastingMachineBlockEntity castingBE) {
                NetworkHooks.openScreen((ServerPlayer) player, castingBE, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CastingMachineBlockEntity castingBE) {
                castingBE.drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}