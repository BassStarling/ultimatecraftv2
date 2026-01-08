package com.bassstarling.ultimatecraftv2.util;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.world.item.ItemStack;

public class SparkStoneUtil {

    public static ItemStack createTier1(){
        ItemStack stack = new ItemStack(ModItems.SPARK_STONE.get());
        stack.getOrCreateTag().putInt("Tier", 1);
        return stack;
    }

    public static ItemStack createTier(int tier) {
        ItemStack stack = new ItemStack(ModItems.SPARK_STONE.get());
        stack.getOrCreateTag().putInt("Tier", tier);
        return stack;
    }
}
