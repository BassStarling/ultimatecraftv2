package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.recipe.IndustrialRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
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
        // レシピの実質的なサイズを計算
        int rMinCol = 5, rMaxCol = -1, rMinRow = 5, rMaxRow = -1;
        for (int i = 0; i < 25; i++) {
            if (!input.get(i).isEmpty()) {
                rMinCol = Math.min(rMinCol, i % 5);
                rMaxCol = Math.max(rMaxCol, i % 5);
                rMinRow = Math.min(rMinRow, i / 5);
                rMaxRow = Math.max(rMaxRow, i / 5);
            }
        }

        // 5x5の背景グリッドを描画（見た目用）
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                // 背景として空のスロット枠だけ描画
                widgets.addSlot(EmiIngredient.of(Ingredient.EMPTY), j * 18, i * 18).drawBack(true);
            }
        }

        // 実質的なレシピ素材のみをスロットとして配置
        // EMIはこの「配置されたスロット」を見て補完位置を決めます
        for (int i = 0; i < 25; i++) {
            EmiIngredient stack = input.get(i);
            if (!stack.isEmpty()) {
                int row = i / 5;
                int col = i % 5;
                // JSONでの位置そのままにスロットを置く
                widgets.addSlot(stack, col * 18, row * 18).drawBack(false);
            }
        }

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 96, 36);
        widgets.addSlot(output.get(0), 122, 32).large(true).recipeContext(this);
    }
}