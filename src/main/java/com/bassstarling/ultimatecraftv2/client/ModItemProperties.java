package com.bassstarling.ultimatecraftv2.client;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModItemProperties {
    public static void register() {
        ItemProperties.register(
                ModItems.SPARK_STONE.get(),
                new ResourceLocation("tier"),
                (stack, level, entity, seed) -> SparkStone.getTier(stack)
        );
        // ここに③を書く
    }
}
