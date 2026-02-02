package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.recipe.IndustrialRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class IndustrialEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public IndustrialEmiRecipe(IndustrialRecipe recipe) {
        this.id = recipe.getId();
        // 25マスの材料を EMI 用のリストに変換
        this.input = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
        // 出力アイテムを変換 (1.20.1)
        this.output = List.of(EmiStack.of(recipe.getResultItem(null)));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        // 後で作るカテゴリを返す
        return IndustrialEmiPlugin.INDUSTRIAL_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() { return 150; } // GUIの幅

    @Override
    public int getDisplayHeight() { return 100; } // GUIの高さ

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 5x5のグリッドを描画 (18ピクセル間隔)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                widgets.addSlot(input.get(i * 5 + j), j * 18, i * 18);
            }
        }

        // 矢印と出力スロット
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 94, 36);
        widgets.addSlot(output.get(0), 120, 32).large(true).recipeContext(this);
    }
}