package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.client.screen.*;
import com.bassstarling.ultimatecraftv2.menu.DustCollectorMenu;
import com.bassstarling.ultimatecraftv2.registry.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
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
            MenuScreens.register(
                    ModMenuTypes.COKE_OVEN_MENU.get(),
                    CokeOvenScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.ARC_FURNACE_MENU.get(),
                    ArcFurnaceScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.CASTING_MACHINE_MENU.get(),
                    CastingMachineScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.DIGESTER_MENU.get(),
                    DigesterScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.FILTER_MENU.get(),
                    FilterScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.CRYSTALLIZER_MENU.get(),
                    CrystallizerScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.UNIVERSALELECTROLYZER_MENU.get(),
                    UniversalElectrolyzerScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.BATTERY_CHARGER_MENU.get(),
                    BatteryChargerScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.AGITATEDTANKRECRYSTALLIZER_MENU.get(),
                    AgitatedTankRecrystallizerScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.DUST_COLLECTOR_MENU.get(),
                    DustCollectorScreen::new
            );

        });

    }
    public static void openMemoScreen(ItemStack stack, InteractionHand hand) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new MemoScreen(stack, hand));
    }
}