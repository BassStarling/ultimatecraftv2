package com.bassstarling.ultimatecraftv2.client;

import com.bassstarling.ultimatecraftv2.client.screen.ElectrolyticFurnaceScreen;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(
                    ModMenuTypes.ELECTROLYTIC_FURNACE_MENU.get(),
                    ElectrolyticFurnaceScreen::new
            );
        });
    }
}
