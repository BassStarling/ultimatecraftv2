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

    // Typeのレジストリ
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, UltimateCraftV2.MOD_ID);


    public static final RegistryObject<RecipeSerializer<IndustrialRecipe>> INDUSTRIAL_SERIALIZER =
            SERIALIZERS.register("industrial_crafting", () -> IndustrialRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<IndustrialRecipe>> INDUSTRIAL_TYPE =
            TYPES.register("industrial_crafting", () -> IndustrialRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CastingRecipe>> CASTING_SERIALIZER =
            SERIALIZERS.register("casting_crafting", () -> CastingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<CastingRecipe>> CASTING_TYPE =
            TYPES.register("casting_crafting", () -> CastingRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<DigestingRecipe>> DIGESTING_SERIALIZER =
            SERIALIZERS.register("digesting", () -> DigestingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<DigestingRecipe>> DIGESTING_TYPE =
            TYPES.register("digesting", () -> DigestingRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<UniversalElectrolyzerRecipe>> UNIVERSALELECTROLYZER_SERIALIZER =
            SERIALIZERS.register("universalelectrolyzer", () -> UniversalElectrolyzerRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<UniversalElectrolyzerRecipe>> UNIVERSALELECTROLYZER_TYPE =
            TYPES.register("universalelectrolyzer", () -> UniversalElectrolyzerRecipe.Type.INSTANCE);


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}