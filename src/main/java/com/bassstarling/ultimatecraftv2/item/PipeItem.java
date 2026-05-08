package com.bassstarling.ultimatecraftv2.item;

import com.bassstarling.ultimatecraftv2.registry.ModSounds;
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
        float randomPitch = 0.9F + player.level().random.nextFloat() * 0.2F; // 0.9 ~ 1.1 の間で変動

        player.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.PIPE_HIT.get(),
                SoundSource.PLAYERS,
                10.0f,  // 音量
                randomPitch // ランダムなピッチ
        );
        return false;
    }
}