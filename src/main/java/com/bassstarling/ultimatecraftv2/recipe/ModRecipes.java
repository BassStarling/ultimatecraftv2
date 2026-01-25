package com.bassstarling.ultimatecraftv2.recipe;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    // Serializerのレジストリ
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, UltimateCraftV2.MOD_ID);

    // Typeのレジストリ (これを忘れるとたまにバグります)
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, UltimateCraftV2.MOD_ID);


    // Serializerの登録
    public static final RegistryObject<RecipeSerializer<IndustrialRecipe>> INDUSTRIAL_SERIALIZER =
            SERIALIZERS.register("industrial_crafting", () -> IndustrialRecipe.Serializer.INSTANCE);

    // Typeの登録
    public static final RegistryObject<RecipeType<IndustrialRecipe>> INDUSTRIAL_TYPE =
            TYPES.register("industrial_crafting", () -> IndustrialRecipe.Type.INSTANCE);


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}