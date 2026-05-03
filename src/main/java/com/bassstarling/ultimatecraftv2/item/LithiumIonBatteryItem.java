package com.bassstarling.ultimatecraftv2.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LithiumIonBatteryItem extends Item {
    public static final int MAX_ENERGY = 600; // 10分分

    public LithiumIonBatteryItem(Properties properties) {
        super(properties.stacksTo(16).durability(MAX_ENERGY));
    }

    // クラフト直後はエネルギー0（Damageが最大）にする
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.setDamageValue(MAX_ENERGY);
    }

    // バーの色をリチウム電池っぽく（青〜水色など）
    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FFF2;
    }
}