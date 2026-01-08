package com.bassstarling.ultimatecraftv2.init;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "ultimatecraftv2");

    public static final RegistryObject<CreativeModeTab> ULTIMATECRAFT_TAB =
            CREATIVE_TABS.register("ultimatecraft_tab", () ->
                    CreativeModeTab.builder()
                            // タブのアイコン
                            .icon(() -> new ItemStack(ModItems.SPARK_STONE.get()))
                            // タブ名（langで翻訳）
                            .title(Component.translatable("creativetab.ultimatecraftv2"))
                            // 表示するアイテム
                            .displayItems((parameters, output) -> {
                                ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                                ModBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                            })
                            .build()

            );

    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
    }
}
