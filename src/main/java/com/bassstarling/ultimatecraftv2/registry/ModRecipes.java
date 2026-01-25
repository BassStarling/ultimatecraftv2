package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.recipe.IndustrialRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, UltimateCraftV2.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, UltimateCraftV2.MOD_ID);

    // ★ 5x5レシピの登録（IndustrialCraftingRecipe クラスなどは作成済みと仮定）
    public static final RegistryObject<RecipeSerializer<IndustrialRecipe>> INDUSTRIAL_CRAFTING_SERIALIZER =
            SERIALIZERS.register("industrial_crafting", () -> IndustrialRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<IndustrialRecipe>> INDUSTRIAL_CRAFTING_TYPE =
            TYPES.register("industrial_crafting", () -> new RecipeType<>() {
                @Override
                public String toString() { return "industrial_crafting"; }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}