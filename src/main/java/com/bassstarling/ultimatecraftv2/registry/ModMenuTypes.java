package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.blockentity.CokeOvenBlockEntity;
import com.bassstarling.ultimatecraftv2.menu.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public static final RegistryObject<MenuType<IndustrialWorkbenchMenu>>
            INDUSTRIAL_WORKBENCH_MENU =
            MENUS.register("industrial_workbench_menu",
                    () -> IForgeMenuType.create(IndustrialWorkbenchMenu::new));

    public static final RegistryObject<MenuType<CokeOvenMenu>> COKE_OVEN_MENU =
            MENUS.register("coke_oven_menu",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos(); // サーバーが書いた pos を読み取る
                        BlockEntity be = inv.player.level().getBlockEntity(pos);
                        if (be instanceof CokeOvenBlockEntity cokeBE) {
                            return new CokeOvenMenu(windowId, inv, cokeBE, cokeBE.getData());
                        }
                        // ここで null を返すと、クライアント側は Menu を維持できず画面を閉じます
                        return null;
                    }));
    public static final RegistryObject<MenuType<CastingMachineMenu>>
            CASTING_MACHINE_MENU =
            MENUS.register("casting_machine_menu",
                    () -> IForgeMenuType.create(CastingMachineMenu::new));

    public static final RegistryObject<MenuType<ArcFurnaceMenu>>
            ARC_FURNACE_MENU =
            MENUS.register("arc_furnace_menu",
                    () -> IForgeMenuType.create(ArcFurnaceMenu::new));

}