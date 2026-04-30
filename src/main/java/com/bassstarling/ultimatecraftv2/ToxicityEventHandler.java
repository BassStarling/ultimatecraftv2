package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//@Mod.EventBusSubscriber(modid = "ultimatecraftv2", bus = Mod.EventBusSubscriber.Bus.FORGE)
//public class ToxicityEventHandler {
//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        // サーバー側でのみ処理。毎ティック判定すると重いので、20ティック（1秒）に1回だけ処理する
//        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide || event.player.tickCount % 20 != 0) {
//            return;
//        }
//
//        Player player = event.player;
//        Level level = player.level();
//        BlockPos pos = player.blockPosition();
//        int radius = 5;
//        boolean foundCinnabar = false;
//
//        // プレイヤーの周囲（半径5ブロックの立方体）をスキャン
//        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
//            // ModBlocks.CINNABAR_ORE はご自身で登録したブロック名に置き換えてください
//            if (level.getBlockState(checkPos).getBlock() == ModBlocks.CINNABAR_ORE.get()) {
//                foundCinnabar = true;
//                break; // 1つでも見つかれば汚染されているのでループを抜ける
//            }
//        }
//
//        if (foundCinnabar) {
//            if (!isWearingFullHazmat(player)) {
//                // 装備が不完全な場合：プレイヤーに容赦のないデバフを与える
//                player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0)); // 5秒間 毒（じわじわ体力を削る）
//                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0)); // 10秒間 吐き気（視界が激しく揺れる）
//                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1)); // 5秒間 強い空腹（走れなくなる）
//            } else {
//                // 全身装備している場合：現実遵守で「防護服自体が劣化する」仕様を追加
//                damageHazmatSuit(player);
//            }
//        }
//    }
//
//    // 全身防護服を着ているかの判定
//    private static boolean isWearingFullHazmat(Player player) {
//        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
//        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
//        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
//        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
//
//        return head.getItem() == ModItems.HAZMAT_MASK.get() &&
//                chest.getItem() == ModItems.HAZMAT_SUIT.get() &&
//                legs.getItem() == ModItems.HAZMAT_LEGGINGS.get() &&
//                feet.getItem() == ModItems.HAZMAT_BOOTS.get();
//    }
//
//    // 毒性環境にいる間、防護服の耐久値を削るメソッド
//    private static void damageHazmatSuit(Player player) {
//        // 毎秒確実に削ると一瞬で壊れてしまうため、10%の確率で耐久度を1減らす
//        if (player.getRandom().nextFloat() < 0.1f) {
//            player.getItemBySlot(EquipmentSlot.HEAD).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.HEAD));
//            player.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
//            player.getItemBySlot(EquipmentSlot.LEGS).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.LEGS));
//            player.getItemBySlot(EquipmentSlot.FEET).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.FEET));
//        }
//    }
//}
