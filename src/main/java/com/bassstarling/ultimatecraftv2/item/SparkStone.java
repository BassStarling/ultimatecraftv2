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

    public static int getTier(ItemStack stack) {
        if (!stack.hasTag()) return 1;
        return stack.getTag().getInt(TIER_KEY);
    }

    public static void setTier(ItemStack stack, int tier) {
        stack.getOrCreateTag().putInt(TIER_KEY, tier);
    }

    @Override
    public void inventoryTick(
            ItemStack stack,
            Level level,
            Entity entity,
            int slotId,
            boolean isSelected
    ) {
        if (!level.isClientSide()) return;

        int tier = getTier(stack);
        stack.getOrCreateTag().putInt("CustomModelData", tier);
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