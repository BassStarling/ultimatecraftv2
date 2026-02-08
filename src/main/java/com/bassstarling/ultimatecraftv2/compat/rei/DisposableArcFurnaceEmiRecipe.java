package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class DisposableArcFurnaceEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public DisposableArcFurnaceEmiRecipe(ResourceLocation id, List<EmiIngredient> inputs, EmiStack outputs) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = List.of(outputs);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.DISPOSABLE_ARC_FURNACE_CATEGORY;
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
        // 入力アイテム（コーク電極とスパークストーン）
        widgets.addSlot(inputs.get(0), 0, 10);
        widgets.addSlot(inputs.get(1), 18, 10);

        // 中央に「アーク炉の上」であることを示すアイコン（アーク炉ブロックを表示）
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 46, 11);
        widgets.addSlot(EmiStack.of(ModBlocks.DISPOSABLE_ARC_FURNACE.get()), 42, -5).drawBack(false);

        // 出力アイテム（グラファイト電極）
        widgets.addSlot(outputs.get(0), 78, 10).large(true).recipeContext(this);
    }
}