package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.client.screen.ElectrolyticFurnaceScreen;
import com.bassstarling.ultimatecraftv2.client.screen.IndustrialWorkbenchScreen;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = UltimateCraftV2.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            System.out.println("DEBUG: Registering MenuScreen...");
            MenuScreens.register(
                    ModMenuTypes.ELECTROLYTIC_FURNACE_MENU.get(),
                    ElectrolyticFurnaceScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.INDUSTRIAL_WORKBENCH_MENU.get(),
                    IndustrialWorkbenchScreen::new
            );
        });
    }
}