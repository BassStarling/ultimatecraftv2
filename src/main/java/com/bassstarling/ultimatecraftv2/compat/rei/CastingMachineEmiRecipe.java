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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class CastingMachineEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public CastingMachineEmiRecipe(ResourceLocation id) {
        this.id = id;
        // 入力: 融解アルミニウム入りバケツ
        this.inputs = List.of(EmiStack.of(ModItems.MOLTEN_ALUMINIUM_IN_BUCKET.get()));
        // 出力: アルミニウムインゴット + 空のバケツ
        this.outputs = List.of(
                EmiStack.of(ModItems.ALUMINIUM_INGOT.get()),
                EmiStack.of(Items.BUCKET)
        );
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.CASTING_CATEGORY;
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 110; }
    @Override public int getDisplayHeight() { return 40; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 入力スロット（融解アルミバケツ）
        widgets.addSlot(inputs.get(0), 10, 12);

        // 鋳造を示す矢印（熱が冷めるイメージで青っぽい色か、通常の矢印）
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 40, 12);

        // 出力スロット1: インゴット
        widgets.addSlot(outputs.get(0), 70, 2).recipeContext(this);
        // 出力スロット2: 空バケツ
        widgets.addSlot(outputs.get(1), 70, 22);

        // 説明テキスト
        widgets.addText(Component.literal("ブロックに右クリック"), 10, 2, 0xAAAAAA, false);
    }
}