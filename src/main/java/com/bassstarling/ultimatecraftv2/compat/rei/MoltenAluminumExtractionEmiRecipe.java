package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
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

public class MoltenAluminumExtractionEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public MoltenAluminumExtractionEmiRecipe(ResourceLocation id) {
        this.id = id;
        // 入力: 使用済み電解炉(ブロック) ＋ 空のバケツ
        this.inputs = List.of(
                EmiStack.of(ModBlocks.USED_ELECTROLYTICFURNACE.get()),
                EmiStack.of(Items.BUCKET)
        );
        // 出力: 融解アルミニウム入りバケツ ＋ 電解炉(ブロックが戻る)
        this.outputs = List.of(
                EmiStack.of(ModItems.MOLTEN_ALUMINIUM_IN_BUCKET.get()),
                EmiStack.of(ModBlocks.ELECTROLYTICFURNACE.get())
        );
    }

    @Override
    public EmiRecipeCategory getCategory() {
        // 電解カテゴリを再利用、もしくは新規作成
        return IndustrialEmiPlugin.ELECTROLYTIC_CATEGORY;
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 120; }
    @Override public int getDisplayHeight() { return 50; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 入力: 使用済みブロックとバケツ
        widgets.addSlot(inputs.get(0), 10, 15);
        widgets.addText(Component.literal("+"), 32, 19, 0xFFFFFF, true);
        widgets.addSlot(inputs.get(1), 42, 15);

        // 回収アクションを示す矢印
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 66, 15);

        // 出力: アルミニウムバケツと、戻った電解炉
        widgets.addSlot(outputs.get(0), 94, 5).recipeContext(this);
        widgets.addSlot(outputs.get(1), 94, 25);

        // 日本語説明
        widgets.addText(Component.literal("右クリックで回収"), 10, 2, 0xAAAAAA, false);
        widgets.addText(Component.literal("⚠ 注意: 素手で触ると火傷します"), 10, 40, 0xFF5555, false);
    }
}