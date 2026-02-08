package com.bassstarling.ultimatecraftv2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TarBucketItem extends Item {
    public TarBucketItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        // 1. クリックしたブロックが「溶岩（源または流動）」かチェック
        if (state.getFluidState().is(FluidTags.LAVA)) {
            if (!level.isClientSide) {
                // 2. 演出（煙と音）
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
                ((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 8, 0.1, 0.1, 0.1, 0.05);

                // 3. アイテムの入れ替え
                if (player != null && !player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                    if (context.getItemInHand().isEmpty()) {
                        player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                    } else {
                        // スタックしている場合（通常は1ですが念のため）はインベントリへ
                        if (!player.getInventory().add(new ItemStack(Items.BUCKET))) {
                            player.drop(new ItemStack(Items.BUCKET), false);
                        }
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}