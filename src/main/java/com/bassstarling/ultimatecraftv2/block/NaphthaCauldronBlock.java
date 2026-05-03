package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.NaphthaCauldronBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class NaphthaCauldronBlock extends Block implements EntityBlock {
    // 形状定義（バニラの大釜と同じ当たり判定にするのが無難です）
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public NaphthaCauldronBlock(Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // パターン1: 右クリックでリチウムを使用
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.LITHIUM_CRYSTAL.get())) {
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                if (level.getBlockEntity(pos) instanceof NaphthaCauldronBlockEntity be) {
                    be.craftRubber(level, pos);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NaphthaCauldronBlockEntity(pos, state);
    }
}