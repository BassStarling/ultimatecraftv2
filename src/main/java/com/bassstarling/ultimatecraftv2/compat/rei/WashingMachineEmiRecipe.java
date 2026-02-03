package com.bassstarling.ultimatecraftv2.compat.rei;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class WashingMachineEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public WashingMachineEmiRecipe(ResourceLocation id, EmiStack input, EmiStack output) {
        this.id = id;
        this.inputs = List.of(EmiStack.of(input.getItemStack()));
        this.outputs = List.of(EmiStack.of(output.getItemStack()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.WASHING_CATEGORY;
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 100; }
    @Override public int getDisplayHeight() { return 40; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 入力スロット
        widgets.addSlot(inputs.get(0), 10, 12);

        // 洗浄中をイメージした水滴のテクスチャや、青い矢印を表示
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 40, 12);
        // 水が必要であることを示すアイコン（水バケツなど）
        widgets.addSlot(EmiStack.of(Items.WATER_BUCKET), 40, -2).drawBack(false);

        // 出力スロット
        widgets.addSlot(outputs.get(0), 74, 12).recipeContext(this);

        widgets.addText(Component.literal("Requires Water Below"), 0, 32, 0x44AAFF, false);
    }
}
