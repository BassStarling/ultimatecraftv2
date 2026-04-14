package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class ReppordBlockEntity extends BlockEntity {
    // 動作間隔 (5tick = 0.25秒に1回。輸送速度は速めに設定)
    private int cooldown = 5;

    public ReppordBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REPPORD_BE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (--cooldown <= 0) {
            if (tryCollectAndUpload()) {
                cooldown = 5;
            } else {
                cooldown = 2; // アイテムがない場合は高速待機
            }
        }
    }

    private boolean tryCollectAndUpload() {
        // 1. 直下 (y-1) のアイテムエンティティをスキャン
        AABB collectArea = new AABB(worldPosition.below());
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, collectArea);

        if (items.isEmpty()) return false;

        // 2. 直上 (y+1) のインベントリを確認
        BlockPos abovePos = worldPosition.above();
        BlockEntity aboveBE = level.getBlockEntity(abovePos);
        if (aboveBE == null) return false;

        // 上のブロックの「下面(DOWN)」からアクセスして搬入を試みる
        // (多くの機械は横や上からですが、汎用コンテナやドロッパーへの搬入も考慮)
        LazyOptional<IItemHandler> cap = aboveBE.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);

        if (cap.isPresent()) {
            IItemHandler handler = cap.orElseThrow(RuntimeException::new);

            for (ItemEntity itemEntity : items) {
                ItemStack stack = itemEntity.getItem();
                if (stack.isEmpty()) continue;

                // インベントリに挿入試行
                ItemStack remain = ItemHandlerHelper.insertItemStacked(handler, stack, false);

                if (remain.getCount() < stack.getCount()) {
                    // 挿入に成功した分だけエンティティの数を減らす
                    itemEntity.setItem(remain);
                    if (remain.isEmpty()) {
                        itemEntity.discard();
                    }

                    // 転送成功時の音とエフェクト
                    level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 2.0F);
                    return true;
                }
            }
        }
        return false;
    }
}