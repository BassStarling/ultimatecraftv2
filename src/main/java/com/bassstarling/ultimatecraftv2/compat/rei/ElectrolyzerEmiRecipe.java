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

    public ElectrolyzerEmiRecipe(ResourceLocation id) {
        this.id = id;
        // 入力: Tier 3の火打石(Spark Stone) + ガラス瓶 + 水(1000mB相当として表示)
        this.inputs = List.of(
                EmiStack.of(ModItems.SPARK_STONE.get()), // 本来はNBTでTier判定が必要
                EmiStack.of(Items.GLASS_BOTTLE),
                EmiStack.of(Fluids.WATER, 1000)
        );
        this.outputs = List.of(EmiStack.of(ModItems.OXYGEN_BOTTLE.get()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.ELECTROLYZER_CATEGORY; // カテゴリは別途定義
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 110; }
    @Override public int getDisplayHeight() { return 40; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // ガラス瓶と火打石（入力アイテム）
        widgets.addSlot(inputs.get(0), 0, 12);
        widgets.addSlot(inputs.get(1), 18, 12);

        // 下にあるべき「水」を背景っぽく配置（またはプラス記号などで表現）
        widgets.addSlot(inputs.get(2), 36, 12).drawBack(false);
        widgets.addText(Component.literal("+"), 40, 2, 0xFFFFFF, true);

        // 矢印
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 12);

        // 出力：酸素入り瓶
        widgets.addSlot(outputs.get(0), 90, 12).recipeContext(this);

        // 特殊条件の注釈
        widgets.addText(Component.literal("Place 💧 under block"), 0, 32, 0xAAAAAA, false);
    }
}
