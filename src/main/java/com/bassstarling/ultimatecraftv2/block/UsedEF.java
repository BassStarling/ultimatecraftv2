package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;

public class UsedEF extends Block {
    public UsedEF(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // 手に「電極(ELECTRODE)」を持っているかチェック
        if (heldItem.is(Items.BUCKET)) {
            if (!level.isClientSide) {
                // 1. 電極を1つ消費
                heldItem.shrink(1);

                // 2. ブロックを「電解炉(通常状態)」に戻す
                level.setBlock(pos, ModBlocks.ELECTROLYTICFURNACE.get().defaultBlockState(), 3);

                // 3. アイテムをプレイヤーに直接渡す (インベントリがいっぱいなら足元に落とす)
                // 融解したアルミニウム(アイテム版)
                ItemStack aluminum = new ItemStack(ModItems.MOLTEN_ALUMINIUM_IN_BUCKET.get());

                ItemHandlerHelper.giveItemToPlayer(player, aluminum);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        else {
            // メインハンドでの操作時かつサーバー側でのみ処理
            if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {

                // 1ダメージ（ハート0.5個分）を与える
                // inFire() は「炎の中にいる」扱いのダメージです。
                // hotFloor()（マグマブロック等の熱ダメージ）なども選べます。
                player.hurt(level.damageSources().inFire(), 1.0f);

                // 演出として、プレイヤーに火をつける（例: 2秒間）ことも可能です（必要ならコメントアウトを外してください）
                // player.setSecondsOnFire(2);
            }

            // ダメージを受けたというフィードバックを返す（腕を振る動作など）
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }
}
