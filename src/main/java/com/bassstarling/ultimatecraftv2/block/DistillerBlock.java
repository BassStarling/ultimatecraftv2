package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.DistillerBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DistillerBlock extends BaseEntityBlock {
    public DistillerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DistillerBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof DistillerBlockEntity tile) {
            if (player.isShiftKeyDown()) {
                // Shift + 右クリック: 増風
                tile.setAirRate(Math.min(100, tile.getAirRate() + 1));
                player.displayClientMessage(Component.literal("🌬️ 送風率: " + tile.getAirRate() + "%"), true);
                level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3f, 1.5f);
                return InteractionResult.SUCCESS;
            } else {
                // 右クリック: ステータス表示
                String status = String.format("🔥燃料:%d 🌬️送風:%d%% 🌡️温度:%.1f℃ 🌑タール:%d",
                        tile.getFuel(), tile.getAirRate(), tile.getTemp(), tile.getTarAmount());
                player.displayClientMessage(Component.literal(status), true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    // 左クリック（減風）の処理
    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof DistillerBlockEntity tile) {
            if (player.isShiftKeyDown()) {
                // Shift + 左クリック: 減風
                tile.setAirRate(Math.max(10, tile.getAirRate() - 1));
                player.displayClientMessage(Component.literal("🌬️ 送風率: " + tile.getAirRate() + "%"), true);
                level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3f, 0.8f);
            } else {
                // 通常の左クリック: 液体回収
                // BlockEntity側に作った回収メソッドを呼び出す
                tile.tryCollectFluid(player, InteractionHand.MAIN_HAND);
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // サーバー側（!isClientSide）でのみ動かす
        if (level.isClientSide) return null;

        // 渡された type が、このブロックが持つ DistillerBlockEntity の型と一致するか確認
        // ModBlockEntities.DISTILLER.get() はご自身の登録名に合わせてください
        return createTickerHelper(type, ModBlockEntities.DISTILLER_BE.get(),
                (lvl, pos, st, be) -> be.serverTick());
    }
}