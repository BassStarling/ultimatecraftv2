package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.menu.ElectrolyticFurnaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, UltimateCraftV2.MOD_ID);

    public static final RegistryObject<MenuType<ElectrolyticFurnaceMenu>>
            ELECTROLYTIC_FURNACE_MENU =
            MENUS.register("electrolytic_furnace_menu",
                    () -> IForgeMenuType.create(ElectrolyticFurnaceMenu::new));

}