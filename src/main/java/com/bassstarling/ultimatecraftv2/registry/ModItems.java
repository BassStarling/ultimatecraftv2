package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.item.SoBolt;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, UltimateCraftV2.MODID);

    public static final RegistryObject<Item> ASSEMBLED_CYLINDER_BLOCK =
            ITEMS.register("assembled_cylinder_block",
                    () -> new Item(new Item.Properties()
                    ));

    public static final RegistryObject<Item> BOLT =
            ITEMS.register("bolt",
                    () -> new SoBolt(new Item.Properties().stacksTo(64)
                    ));
    public static final RegistryObject<Item> SPARK_DUST =
            ITEMS.register("spark_dust",
                    () -> new Item(new Item.Properties()
                    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
