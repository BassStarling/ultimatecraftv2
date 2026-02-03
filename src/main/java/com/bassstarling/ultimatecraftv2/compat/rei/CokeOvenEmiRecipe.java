package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.checkerframework.checker.nullness.qual.Nullable;
import dev.emi.emi.api.widget.Widget;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CokeOvenEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final boolean isBucketRecipe;

    public CokeOvenEmiRecipe(ResourceLocation id, EmiIngredient input, EmiStack resultItem, EmiStack resultFluid, boolean isBucket) {
        this.id = id;
        this.inputs = List.of(input);
        this.isBucketRecipe = isBucket;
        this.outputs = List.of(resultItem, resultFluid);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return IndustrialEmiPlugin.COKE_OVEN_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() { return id; }
    @Override
    public List<EmiIngredient> getInputs() { return inputs; }
    @Override
    public List<EmiStack> getOutputs() { return outputs; }
    @Override
    public int getDisplayWidth() { return 110; }
    @Override
    public int getDisplayHeight() { return 42; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 基本レイアウト
        widgets.addSlot(inputs.get(0), 10, 2);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 36, 12);
        widgets.addSlot(outputs.get(0), 62, 12).recipeContext(this);

        if (isBucketRecipe) {
            // アニメーションとツールチップの描画
            widgets.addDrawable(86, 2, 18, 40, (graphics, mouseX, mouseY, delta) -> {
                // 2000ms周期 (1秒ごとに切り替え)
                boolean isFull = (System.currentTimeMillis() / 1000) % 2 == 0;

                // 表示すべきStackを決定
                EmiStack tankStack = isFull ? outputs.get(1) : EmiStack.EMPTY;
                EmiStack bucketStack = isFull ? EmiStack.of(Items.BUCKET) : EmiStack.of(ModItems.TAR_BUCKET.get());

                // 1. アイテムの描画
                drawSlotStack(graphics, tankStack, 0, 0);
                drawSlotStack(graphics, bucketStack, 0, 20);

                // 2. ツールチップの描画 (Minecraft標準の機能を使用)
                // mouseX, mouseY はこのエリア(86, 2)からの相対座標
                if (mouseX >= 0 && mouseX < 18) {
                    if (mouseY >= 0 && mouseY < 18 && !tankStack.isEmpty()) {
                        // 上段：液体 (tankStack)
                        graphics.renderTooltip(Minecraft.getInstance().font, tankStack.getTooltipText(), Optional.empty(), mouseX, mouseY);
                    } else if (mouseY >= 20 && mouseY < 38) {
                        // 下段：バケツ (bucketStack)
                        graphics.renderTooltip(Minecraft.getInstance().font, bucketStack.getTooltipText(), Optional.empty(), mouseX, mouseY);
                    }
                }
            });
            widgets.add(new CycleWidget(86, 2, outputs.get(1), EmiStack.of(ModItems.TAR_BUCKET.get())));
            widgets.addText(Component.literal("Full Cycle"), 10, 32, 0x888888, false);
        } else {
            // 通常レシピ (250mB)
            widgets.addSlot(outputs.get(1), 86, 2);
            widgets.addText(Component.literal("250 mB"), 82, 22, 0x888888, false);
        }
    }

    private static class CycleWidget extends Widget {
        private final int x, y;
        private final EmiStack tankStack;
        private final EmiStack bucketStack;

        public CycleWidget(int x, int y, EmiStack tankStack, EmiStack bucketStack) {
            this.x = x;
            this.y = y;
            this.tankStack = tankStack;
            this.bucketStack = bucketStack;
        }

        @Override
        public Bounds getBounds() {
            // ウィジェットの当たり判定 (x, y, width, height)
            return new Bounds(x, y, 18, 40);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            // 2000ms周期 (1秒ごとに切り替え)
            boolean isFull = (System.currentTimeMillis() / 1000) % 2 == 0;

            if (isFull) {
                drawSlot(graphics, tankStack, x, y);
                drawSlot(graphics, EmiStack.of(Items.BUCKET), x, y + 20);
            } else {
                drawSlot(graphics, EmiStack.EMPTY, x, y);
                drawSlot(graphics, bucketStack, x, y + 20);
            }
        }

        @Override
        public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
            boolean isFull = (System.currentTimeMillis() / 1000) % 2 == 0;
            List<Component> text;

            // 1. 表示するテキストを選択
            if (mouseY < 18) {
                if (!isFull) return List.of();
                text = tankStack.getTooltipText();
            } else if (mouseY >= 20 && mouseY < 40) {
                EmiStack b = isFull ? EmiStack.of(Items.BUCKET) : EmiStack.of(ModItems.TAR_BUCKET.get());
                text = b.getTooltipText();
            } else {
                return List.of();
            }

            // 2. Component のリストを ClientTooltipComponent のリストに変換して返す
            // これが EMI (およびバニラ 1.19+) の期待する型です
            return text.stream()
                    .map(Component::getVisualOrderText)
                    .map(ClientTooltipComponent::create)
                    .collect(Collectors.toList());
        }

        private void drawSlot(GuiGraphics graphics, EmiStack stack, int dx, int dy) {
            EmiTexture.SLOT.render(graphics, dx, dy, 0);
            stack.render(graphics, dx + 1, dy + 1, 0);
        }
    }

    private void drawSlotStack(GuiGraphics graphics, EmiStack stack, int x, int y) {
        // EMIのスロット背景を描画
        EmiTexture.SLOT.render(graphics, x, y, 0);
        // アイテム/液体を描画
        stack.render(graphics, x + 1, y + 1, 0);
    }
}