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

public class ArcFurnaceEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public ArcFurnaceEmiRecipe(ResourceLocation id) {
        this.id = id;

        // 入力1: コークス電極
        EmiIngredient cokeElectrode = EmiStack.of(ModItems.COKE_ELECTRODE.get());

        // 入力2: スパークストーン（全Tierをサイクル表示させるのがベスト）
        // もし NBT ごとに別アイテムとして定義していないなら、タグ等でまとめます
        EmiIngredient sparkStones = EmiIngredient.of(List.of(
                EmiStack.of(ModItems.SPARK_STONE.get()) // 必要に応じてNBT付与したスタックを並べる
        ));

        this.inputs = List.of(cokeElectrode, sparkStones);
        this.outputs = List.of(EmiStack.of(ModItems.GRAPHITE_ELECTRODE.get()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.ARC_FURNACE_CATEGORY;
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 120; }
    @Override public int getDisplayHeight() { return 40; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 背景の矢印（進行状況アニメーション 10秒 = 10000ms）
        widgets.addFillingArrow(46, 12, 10000);

        // スロット0: 入力 (コークス電極)
        widgets.addSlot(inputs.get(0), 18, 12);

        // スロット1: 出力 (グラファイト電極)
        widgets.addSlot(outputs.get(0), 82, 12).recipeContext(this);

        // スロット2: 燃料 (スパークストーン)
        // 少し下に配置して「エネルギー源」であることを示す
        widgets.addSlot(inputs.get(1), 46, 22).drawBack(false);

        // 雷マークなどのテクスチャがあればここに追加すると雰囲気が出ます
        widgets.addText(Component.literal("⚡"), 50, 2, 0xFFD700, true);
    }
}