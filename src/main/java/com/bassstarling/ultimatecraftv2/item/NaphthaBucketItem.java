package com.bassstarling.ultimatecraftv2.item;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class NaphthaBucketItem extends Item {
    public NaphthaBucketItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);

        // 1. クリックされたブロックがバニラの大釜かチェック
        if (state.is(Blocks.CAULDRON)) {
            if (!level.isClientSide) {
                // 2. ナフサ入り大釜に置き換え
                level.setBlockAndUpdate(pos, ModBlocks.NAPHTHA_CAULDRON.get().defaultBlockState());

                // 3. バケツを空にする（クリエイティブ以外）
                if (player != null && !player.getAbilities().instabuild) {
                    // 基本的にバケツは1個ずつ使う想定ですが、スタックされている場合を考慮
                    ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.setItemInHand(context.getHand(), emptyBucket);
                    } else if (!player.getInventory().add(emptyBucket)) {
                        player.drop(emptyBucket, false);
                    }
                }

                // 4. 音を鳴らす
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}