package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.registry.ModArmorMaterials;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "ultimatecraftv2")
public class ModBlockEvents {

    @SubscribeEvent
    public static void onCinnabarMining(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        BlockState state = level.getBlockState(pos);

        // 1. 破壊しようとしているブロックが辰砂鉱石かチェック
        // (BlockItemなどから取得するか、直接Blockを指定してください)
        if (state.is(ModBlocks.CINNABAR_ORE.get())) {

            // 2. ヘルメットスロットを確認し、粉塵マスク（DUSTマテリアル）を装備しているか判定
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            boolean hasMask = !helmet.isEmpty() &&
                    helmet.getItem() instanceof ArmorItem armor &&
                    armor.getMaterial() == ModArmorMaterials.DUST;

            // 3. マスクがない場合、ウィザー状態を付与
            if (!hasMask) {
                // 既に強いウィザーがかかっていないか確認（エフェクトの重複防止）
                if (!player.hasEffect(MobEffects.WITHER) || player.getEffect(MobEffects.WITHER).getDuration() < 20) {
                    // ウィザー I を 5秒間 (100 ticks) 付与
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                }

                // 4. 演出：粉塵が舞っているパーティクルを出す
                if (level.isClientSide) {
                    for(int i = 0; i < 3; i++) {
                        level.addParticle(ParticleTypes.ASH,
                                pos.getX() + level.random.nextDouble(),
                                pos.getY() + level.random.nextDouble(),
                                pos.getZ() + level.random.nextDouble(),
                                0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }
}