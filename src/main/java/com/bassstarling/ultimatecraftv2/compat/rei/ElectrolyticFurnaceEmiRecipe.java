package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class ElectrolyticFurnaceEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public ElectrolyticFurnaceEmiRecipe(ResourceLocation id) {
        this.id = id;
        // 1. 氷晶石 2. アルミナ 3. Tier5スパーク(融解用) 4. Tier6スパーク(電解用)
        this.inputs = List.of(
                EmiStack.of(ModItems.CRYOLITE.get()),
                EmiStack.of(ModItems.ALUMINA.get()),
                EmiStack.of(getSparkStone(5)),
                EmiStack.of(getSparkStone(6))
        );
        // 成果物はアイテムではなく「使用済み電解炉」ブロック
        this.outputs = List.of(EmiStack.of(ModBlocks.USED_ELECTROLYTICFURNACE.get()));
    }

    private static ItemStack getSparkStone(int tier) {
        ItemStack stack = new ItemStack(ModItems.SPARK_STONE.get());
        stack.getOrCreateTag().putInt("Tier", tier);
        return stack;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.ELECTROLYTIC_CATEGORY;
    }

    @Override public @Nullable ResourceLocation getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 144; }
    @Override public int getDisplayHeight() { return 80; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 背景スロット配置
        widgets.addSlot(inputs.get(0), 2, 2); // 氷晶石
        widgets.addSlot(inputs.get(1), 2, 22); // アルミナ
        widgets.addText(Component.literal("+"), 22, 12, 0xFFFFFF, true);

        widgets.addSlot(inputs.get(2), 32, 2); // Tier 5
        widgets.addSlot(inputs.get(3), 32, 22); // Tier 6

        // 工程矢印 (電解工程 20秒)
        widgets.addFillingArrow(54, 12, 20000);

        // 最終成果物（ブロックそのものが変化することを表現）
        widgets.addSlot(outputs.get(0), 90, 12).recipeContext(this);

        // 日本語での工程説明
        int y = 45;
        widgets.addText(Component.literal("① 氷晶石とTier 5で融解を開始"), 2, y, 0xAAAAAA, false);
        widgets.addText(Component.literal("② アルミナ投入後、Tier 6で電解"), 2, y + 10, 0xAAAAAA, false);
        widgets.addText(Component.literal("③ 完了後、ブロックが「使用済み」へ変化"), 2, y + 20, 0xFF5555, false);

        // 警告マーク
        widgets.addText(Component.literal("⚠ 再利用不可"), 110, 14, 0xFF0000, false);
    }
}