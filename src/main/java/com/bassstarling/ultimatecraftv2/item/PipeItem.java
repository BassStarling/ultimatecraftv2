package com.bassstarling.ultimatecraftv2.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class PipeItem extends SwordItem {
    public PipeItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

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
        return false;
    }
}