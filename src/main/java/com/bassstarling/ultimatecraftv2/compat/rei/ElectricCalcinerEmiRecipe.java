package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class ElectricCalcinerEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public ElectricCalcinerEmiRecipe(ResourceLocation id, EmiStack input, EmiStack output) {
        this.id = id;
        // 入力: 洗浄済みボーキサイト粉 + スパークストーン (Tier 4)
        this.inputs = List.of(
                input,
                EmiStack.of(ModItems.SPARK_STONE.get()) // ※内部でTier 4を表示
        );
        this.outputs = List.of(output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.CALCINER_CATEGORY;
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 100; }
    @Override public int getDisplayHeight() { return 44; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 入力スロット（材料）
        widgets.addSlot(inputs.get(0), 0, 10);

        // プラス記号
        widgets.addText(Component.literal("+"), 20, 14, 0xFFFFFF, true);

        // スパークストーンスロット（Tier 4であることを強調）
        widgets.addSlot(inputs.get(1), 30, 10);

        // 燃焼をイメージした矢印
        widgets.addFillingArrow(54, 10, 5000); // 仮焼（焼成）なので少し早めの演出

        // 出力スロット（アルミナ）
        widgets.addSlot(outputs.get(0), 80, 10).recipeContext(this);

        // Tier 4 指定のテキスト
        widgets.addText(Component.literal("Tier 4 Required"), 0, 32, 0xFF5555, false);
    }
}