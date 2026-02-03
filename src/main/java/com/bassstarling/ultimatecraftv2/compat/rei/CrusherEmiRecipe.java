package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class CrusherEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public CrusherEmiRecipe(ResourceLocation id, EmiStack input, EmiStack output) {
        this.id = id;
        // 入力: 原料 + スパークストーン (Tier 1)
        this.inputs = List.of(
                EmiStack.of(input.getItemStack()),
                EmiStack.of(ModItems.SPARK_STONE.get()) // 本来はNBTでTier1を指定
        );
        this.outputs = List.of(EmiStack.of(output.getItemStack()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.CRUSHER_CATEGORY; // あとで定義
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 100; }
    @Override public int getDisplayHeight() { return 40; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 入力スロット（原料と火打石）
        widgets.addSlot(inputs.get(0), 0, 12);
        widgets.addText(Component.literal("+"), 20, 16, 0xFFFFFF, true);
        widgets.addSlot(inputs.get(1), 30, 12);

        // 粉砕をイメージした矢印
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 54, 12);

        // 出力スロット
        widgets.addSlot(outputs.get(0), 80, 12).recipeContext(this);

        // Tier条件の注釈
        widgets.addText(Component.literal("Tier 1 Required"), 0, 32, 0xAAAAAA, false);
    }
}
