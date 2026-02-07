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
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlakedLimeEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public SlakedLimeEmiRecipe(ResourceLocation id) {
        this.id = id;
        // 入力: 生石灰(Item) + 水(Fluid)
        this.inputs = List.of(
                EmiStack.of(ModItems.QUICK_LIME.get()),
                EmiStack.of(Fluids.WATER) // ここを液体（水源）に変更
        );
        // 出力: 消石灰のみ（バケツではないので返却物もなし）
        this.outputs = List.of(
                EmiStack.of(ModItems.SLAKED_LIME.get())
        );
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.CHEMICAL_CATEGORY; }

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
    public int getDisplayWidth() {
        return 135;
    }

    @Override
    public int getDisplayHeight() {
        return 35;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 1. 生石灰
        widgets.addSlot(inputs.get(0), 10, 12);

        // 2. 水源（プラスアイコンを添えて「混ぜる」感を出すことも可能）
        widgets.addText(Component.literal("+"), 32, 16, 0xFFFFFF, false);

        // 水のスロット。amountを表示しない設定にすると「水源」っぽさが増します
        widgets.addSlot(inputs.get(1), 40, 12);

        // 矢印
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 65, 12);

        // 結果：消石灰
        widgets.addSlot(outputs.get(0), 95, 12).recipeContext(this);

        // 説明テキスト（多言語対応のため翻訳キー推奨）
        widgets.addText(Component.translatable("tooltip.ultimatecraftv2.water_interaction"), 10, 2, 0xAAAAAA, false);
    }
}