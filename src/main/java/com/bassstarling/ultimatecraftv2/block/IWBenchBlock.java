package com.bassstarling.ultimatecraftv2.block;

import com.bassstarling.ultimatecraftv2.blockentity.IndustrialWorkbenchBlockEntity;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IWBenchBlock extends Block implements EntityBlock {
    public IWBenchBlock(Properties p_49795_) {
        super(p_49795_);
    }
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // EntityBlockにするとモデルが消えるのを防ぐ
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        System.out.println("Right clicked! Level isClientSide: " + level.isClientSide);

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof IndustrialWorkbenchBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) blockentity, pos);
            }
            return InteractionResult.CONSUME;
        }
    }
    // 下のブロックが透けて見えるように、遮蔽計算を無効にする
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    // ブロックが不透明な立方体でないことを伝える（これがないと隣接面が描画されない）
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    // 周囲のブロック（地面）を「描画しなくていいよ」と勘違いさせないための設定
    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    // これが「不透明なフルブロックではない」ことを示す最も重要なメソッド
    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // 自分のBlockEntityを生成して返す
        return ModBlockEntities.INDUSTRIAL_WORKBENCH.get().create(pos, state);
    }
}