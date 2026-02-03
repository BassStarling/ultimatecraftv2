package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.recipe.CastingRecipe;
import com.bassstarling.ultimatecraftv2.recipe.IndustrialRecipe;
import com.bassstarling.ultimatecraftv2.recipe.ModRecipes;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class IndustrialEmiPlugin implements EmiPlugin {

    public static final EmiRecipeCategory INDUSTRIAL_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "industrial_crafting"),
            EmiStack.of(ModBlocks.INDUSTRIAL_WORKBENCH.get())
    );

    public static final EmiRecipeCategory CASTING_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "casting_crafting"),
            EmiStack.of(ModBlocks.CASTING_MACHINE.get())
    );

    public static final EmiRecipeCategory OXYGEN_CONVERTER_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "converter"),
            EmiStack.of(ModBlocks.OXYGEN_CONVERTER.get())
    );

    public static final EmiRecipeCategory COKE_OVEN_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "coke_oven"),
            EmiStack.of(ModBlocks.COKEOVEN.get())
    );

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(OXYGEN_CONVERTER_CATEGORY);
        registry.addWorkstation(OXYGEN_CONVERTER_CATEGORY, EmiStack.of(ModBlocks.OXYGEN_CONVERTER.get()));

        registry.addRecipe(new OxygenConverterEmiRecipe());

        registry.addCategory(INDUSTRIAL_CATEGORY);

        registry.addWorkstation(INDUSTRIAL_CATEGORY, EmiStack.of(ModBlocks.INDUSTRIAL_WORKBENCH.get()));

        RecipeManager manager = registry.getRecipeManager();
        for (IndustrialRecipe recipe : manager.getAllRecipesFor(ModRecipes.INDUSTRIAL_TYPE.get())) {
            registry.addRecipe(new IndustrialEmiRecipe(recipe));
        }

        registry.addCategory(CASTING_CATEGORY);

        registry.addWorkstation(CASTING_CATEGORY, EmiStack.of(ModBlocks.CASTING_MACHINE.get()));

        for (CastingRecipe recipe : manager.getAllRecipesFor(ModRecipes.CASTING_TYPE.get())) {
            registry.addRecipe(new CastingEmiRecipe(recipe));
        }

        registry.addCategory(COKE_OVEN_CATEGORY);

        registry.addWorkstation(COKE_OVEN_CATEGORY, EmiStack.of(ModBlocks.COKEOVEN.get()));

        // 通常レシピ
        registry.addRecipe(new CokeOvenEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "coke_oven/coal_coke"),
                EmiIngredient.of(Ingredient.of(Items.COAL)),
                EmiStack.of(ModItems.COKE.get()),
                EmiStack.of(ModFluids.SOURCE_TAR.get(), 250),
                false // isBucket = false
        ));

// バケツレシピ
        registry.addRecipe(new CokeOvenEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "coke_oven/tar_bucket"),
                EmiIngredient.of(Ingredient.of(Items.COAL), 4),
                EmiStack.of(ModItems.COKE.get(), 4),
                EmiStack.of(ModFluids.SOURCE_TAR.get(), 1000), // 1000mB
                true // isBucket = true
        ));
    }
}