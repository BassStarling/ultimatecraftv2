package com.bassstarling.ultimatecraftv2.item;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SparkStone extends Item {

    public static final String TIER_KEY = "tier";

    public SparkStone(Properties p_41383_) {
        super(p_41383_);
    }

    // --- ここを追加 ---
    @Override
    public boolean isFoil(ItemStack stack) {
        int tier = getTier(stack);
        // Tier 6 または Tier 7 のときだけキラキラさせる
        return tier == 6 || tier == 7;
    }
    // ------------------

    public static int getTier(ItemStack stack) {
        if (!stack.hasTag()) return 1;
        return stack.getTag().getInt(TIER_KEY);
    }

    public static void setTier(ItemStack stack, int tier) {
        stack.getOrCreateTag().putInt(TIER_KEY, tier);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // サーバー・クライアント両方で整合性を取るため isClientSide 判定を外すか、
        // もしくは現状のままでも動作しますが、NBT更新はサーバー主導が一般的です。
        if (!level.isClientSide()) {
            int tier = getTier(stack);
            if (stack.getOrCreateTag().getInt("CustomModelData") != tier) {
                stack.getOrCreateTag().putInt("CustomModelData", tier);
            }
            return;
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        int tier = getTier(stack);
        String key = "item.ultimatecraftv2.sparkstone.tier" + tier;
        return Component.translatable(key);
    }

    public static ItemStack createWithTier(int tier) {
        ItemStack stack = new ItemStack(ModItems.SPARK_STONE.get());
        setTier(stack, tier);
        return stack;
    }
}