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
import net.minecraft.world.item.crafting.Ingredient;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class OxygenConverterEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public OxygenConverterEmiRecipe() {
        this.id = new ResourceLocation("ultimatecraftv2", "oxygen_converting/steel");

        // 入力アイテムをリスト化
        this.inputs = List.of(
                EmiIngredient.of(Ingredient.of(ModItems.SPARK_STONE.get())),
                EmiIngredient.of(Ingredient.of(ModItems.OXYGEN_BOTTLE.get())),
                EmiIngredient.of(Ingredient.of(ModItems.PIG_IRON.get()))
        );

        // 出力アイテム（鋼鉄 + 副産物の空瓶）
        this.outputs = List.of(
                EmiStack.of(ModItems.STEEL_INGOT.get()),
                EmiStack.of(Items.GLASS_BOTTLE)
        );
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.OXYGEN_CONVERTER_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() { return id; }
    @Override
    public List<EmiIngredient> getInputs() { return inputs; }
    @Override
    public List<EmiStack> getOutputs() { return outputs; }
    @Override
    public int getDisplayWidth() {
        // スロット(112 + 18) くらいがちょうど良い幅です
        return 135;
    }

    @Override
    public int getDisplayHeight() {
        // 縦幅を少し詰めると余白が消えて締まります
        return 35;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 入力 (0, 20, 40)
        widgets.addSlot(inputs.get(0), 0, 4);
        widgets.addSlot(inputs.get(1), 20, 4);
        widgets.addSlot(inputs.get(2), 40, 4);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 66, 4);

        // 出力 (92, 112)
        widgets.addSlot(outputs.get(0), 92, 4).recipeContext(this);
        widgets.addSlot(outputs.get(1), 112, 4);

        // テキストを少し下に離す
        widgets.addText(Component.literal("Drop on Converter"), 22, 26, 0x888888, false);
    }
}