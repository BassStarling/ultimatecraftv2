package com.bassstarling.ultimatecraftv2.compat.rei;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class SparkCompressorEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public SparkCompressorEmiRecipe(ResourceLocation id, List<EmiIngredient> inputs, EmiStack output) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = List.of(output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        // 後述するカスタムカテゴリーを指定
        return IndustrialEmiPlugin.SPARK_COMPRESSOR_CATEGORY;
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
        // 背景や矢印、アイコンを描画
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 40, 10);

        // スロットの配置
        widgets.addSlot(inputs.get(0), 0, 10);
        if (inputs.size() > 1) {
            widgets.addSlot(inputs.get(1), 18, 10);
        }

        // 出力スロット
        widgets.addSlot(outputs.get(0), 70, 10).recipeContext(this);
    }
}