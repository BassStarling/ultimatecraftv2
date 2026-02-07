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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class ElectrolyzerEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    // --- パターンA: 引数1つの既存コンストラクタ（酸素ボトル用など） ---
    public ElectrolyzerEmiRecipe(ResourceLocation id) {
        this.id = id;
        // 酸素ボトルのデフォルト設定
        this.inputs = List.of(
                EmiStack.of(Items.GLASS_BOTTLE),
                EmiStack.of(ModItems.SPARK_STONE.get()) // ティア3
        );
        this.outputs = List.of(EmiStack.of(ModItems.OXYGEN_BOTTLE.get()));
    }

    // --- パターンB: 引数4つの新しいコンストラクタ（今回追加分） ---
    public ElectrolyzerEmiRecipe(ResourceLocation id, EmiIngredient input, EmiIngredient catalyst, EmiStack output) {
        this.id = id;
        this.inputs = List.of(input, catalyst);
        this.outputs = List.of(output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.ELECTROLYZER_CATEGORY;
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 110; }
    @Override public int getDisplayHeight() { return 40; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 下に水があることを示す背景やアイコン（任意）
        widgets.addSlot(EmiStack.of(Fluids.WATER), 40, 25).drawBack(false);

        // 入力アイテム
        widgets.addSlot(inputs.get(0), 10, 12);
        // スパークストーン
        widgets.addSlot(inputs.get(1), 30, 12);

        // 矢印
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 55, 12);

        // 結果
        widgets.addSlot(outputs.get(0), 85, 12).recipeContext(this);
    }
}