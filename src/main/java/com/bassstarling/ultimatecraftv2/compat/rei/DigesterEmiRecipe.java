package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.recipe.DigestingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DigesterEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final int cookTime;

    public DigesterEmiRecipe(DigestingRecipe recipe) {
        this.id = recipe.getId();

        // アイテム入力: EmiStack.of を使用
        EmiIngredient itemInput = EmiStack.of(recipe.getInputItem());

        // 液体入力: 液体オブジェクトと量から生成
        EmiIngredient fluidInput = EmiStack.of(recipe.getInputFluid().getFluid(), recipe.getInputFluid().getAmount());

        // 入力リストにまとめる
        this.inputs = List.of(itemInput, fluidInput);

        // 出力
        this.outputs = List.of(
                EmiStack.of(recipe.getOutputFluid().getFluid(), recipe.getOutputFluid().getAmount())
        );

        this.cookTime = recipe.getCookTime();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.DIGESTER_CATEGORY;
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
    public int getDisplayWidth() { return 170; }

    @Override
    public int getDisplayHeight() { return 80; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation GUI_TEXTURE = new ResourceLocation("ultimatecraftv2", "textures/gui/digester.png");

        // 矢印のアニメーション (cookTimeをミリ秒に変換するために係数を調整)
        widgets.addFillingArrow(80, 42, cookTime * 50);

        // アイテム入力 (inputsの0番目)
        widgets.addSlot(inputs.get(0), 56, 61);

        // 液体入力タンク (inputsの1番目)
        // 描画位置はScreenの調整に合わせる [16, 17]
        widgets.addSlot(inputs.get(1), 16, 17).drawBack(false);

        // 液体出力タンク (outputsの0番目) [112, 17]
        widgets.addSlot(outputs.get(0), 112, 17).drawBack(false).recipeContext(this);
    }
}