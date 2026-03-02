package com.bassstarling.ultimatecraftv2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TarBucketItem extends Item {
    public TarBucketItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 1. 視線の先にある「液体」を含めてヒットテストを行う
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (blockhitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        }

        if (blockhitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockhitresult.getBlockPos();
            BlockState state = level.getBlockState(pos);

            // 2. クリックした場所が溶岩かどうかを判定
            if (state.getFluidState().is(FluidTags.LAVA)) {
                if (!level.isClientSide) {
                    // 演出（音とパーティクル）
                    level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
                    ((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 8, 0.1, 0.1, 0.1, 0.05);

                    // アイテムの入れ替え処理
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        ItemStack bucket = new ItemStack(Items.BUCKET);
                        if (itemstack.isEmpty()) {
                            return InteractionResultHolder.success(bucket);
                        }
                        if (!player.getInventory().add(bucket)) {
                            player.drop(bucket, false);
                        }
                    }
                }
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
            }
        }

        return InteractionResultHolder.pass(itemstack);
    }

    // useメソッドを使う場合は useOn は PASS させておくだけでOKです
    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }
}