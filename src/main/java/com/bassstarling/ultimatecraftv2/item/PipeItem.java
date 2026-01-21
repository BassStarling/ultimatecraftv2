package com.bassstarling.ultimatecraftv2.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class PipeItem extends SwordItem {
    // コンストラクタ
    public PipeItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }
    /*
     * ステータスの計算式解説:
     * * 最終攻撃力 = 1 (素手) + 素材の攻撃力 (2.0) + ここで指定する追加攻撃力
     * 最終攻撃速度 = 4.0 (基本) + ここで指定する補正値 (マイナスにする)
     */

    /**
     * エンティティを左クリック（攻撃）した時に呼ばれる
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        // 攻撃した瞬間に音を鳴らす
        // player.level() は 1.20.1 での記述
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_PLACE, // ここで音の種類を指定（例：金床を置く音）
                SoundSource.PLAYERS,
                0.5f,  // 音量
                1.5f   // ピッチ（高いほど鋭い音になる）
        );

        // falseを返すと、通常のダメージ処理もそのまま継続される
        return false;
    }
}
