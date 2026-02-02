package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.recipe.CastingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class CastingEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public CastingEmiRecipe(CastingRecipe recipe) {
        this.id = recipe.getId();

        this.inputs = List.of(
                EmiIngredient.of(recipe.getIngredients().get(0)), // inputA
                EmiIngredient.of(recipe.getIngredients().get(1))  // inputB
        );
        this.outputs = List.of(EmiStack.of(recipe.getResultItem(null)));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.CASTING_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() { return 100; }

    @Override
    public int getDisplayHeight() { return 40; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 入力スロット2つを横に並べる
        widgets.addSlot(inputs.get(0), 0, 10);
        widgets.addSlot(inputs.get(1), 18, 10);

        // 中央に矢印
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 46, 10);

        // 出力スロット
        widgets.addSlot(outputs.get(0), 76, 6).large(true).recipeContext(this);
    }
}