package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.menu.DigesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class DigesterScreen extends AbstractContainerScreen<DigesterMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/digester.png");

    public DigesterScreen(DigesterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 170;

        this.titleLabelY = 3;

        this.inventoryLabelY = 78;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // --- 入力タンク描画 ---
        // menu.getContainerData().get(2) を使って「量」を取得
        int inputAmt = this.menu.getContainerData().get(2); // 2番は入力タンク
        int outputAmt = this.menu.getContainerData().get(3); // 3番は出力タンク
        // クライアント表示用に一時的なFluidStackを作成（色取得用）
        FluidStack inputViewStack = new FluidStack(ModFluids.SOURCE_SODIUM_HYDROXIDE.get(), inputAmt);
        int inputColor = getFluidColor(inputViewStack, 0xFFE0E0E0);
        renderFluidBar(guiGraphics, inputAmt, 10000, x + 16, y + 17, inputColor);

        // --- 出力タンク描画 ---
        FluidStack outputViewStack = new FluidStack(ModFluids.SOURCE_SODIUM_ALUMINATE_SUSPENSION.get(), outputAmt);
        int outputColor = getFluidColor(outputViewStack, 0xFFD2B48C);
        renderFluidBar(guiGraphics, outputAmt, 10000, x + 112, y + 17, outputColor);

        // 矢印
        if (this.menu.isProcessing()) {
            guiGraphics.blit(TEXTURE, x + 79, y + 38, 176, 17, 24, 17);
        }
    }

    /**
     * 液体から色を取得するヘルパーメソッド
     */
    private int getFluidColor(FluidStack stack, int defaultColor) {
        if (stack.isEmpty()) return defaultColor;

        // IClientFluidTypeExtensions を介して液体の色を取得します
        // これにより、FluidType で定義した tintColor が反映されます
        int color = IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack);

        // もし色が 0 (透明) ならデフォルトを返す
        return color == 0 ? defaultColor : color;
    }

    /**
     * 液体タンクを簡易的に描画するメソッド
     */
    private void renderFluidBar(GuiGraphics guiGraphics, int amount, int capacity, int x, int y, int color) {
        if (amount <= 0) return;

        int tankHeight = 60;
        int fluidHeight = (int) ((float) amount / capacity * tankHeight);

        guiGraphics.fill(x, y + tankHeight - fluidHeight, x + 37, y + tankHeight, color);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // タンクにマウスを合わせた時に量を表示するツールチップ
        renderTankTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderTankTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // ContainerData から確実に同期されている数値を直接取得
        int inputAmount = menu.getContainerData().get(2);
        int outputAmount = menu.getContainerData().get(3);

        // 1. 入力タンク (左) のツールチップ
        if (mouseX >= x + 16 && mouseX <= x + 52 && mouseY >= y + 15 && mouseY <= y + 75) {
            // クライアント側で表示用のFluidStackを生成して渡す
            FluidStack inputStack = new FluidStack(ModFluids.SOURCE_SODIUM_HYDROXIDE.get(), inputAmount);
            renderFluidTooltip(guiGraphics, inputStack, 10000, mouseX, mouseY);
        }

        // 2. 出力タンク (右) のツールチップ
        if (mouseX >= x + 112 && mouseX <= x + 148 && mouseY >= y + 15 && mouseY <= y + 75) {
            // 出力側のFluidStackを生成して渡す
            FluidStack outputStack = new FluidStack(ModFluids.SOURCE_SODIUM_ALUMINATE_SUSPENSION.get(), outputAmount);
            renderFluidTooltip(guiGraphics, outputStack, 10000, mouseX, mouseY);
        }
    }

    /**
     * 液体タンクの中身に応じて動的に翻訳された名前と量を表示する
     * 引数を FluidTank から FluidStack と最大容量(capacity) に変更
     */
    private void renderFluidTooltip(GuiGraphics guiGraphics, FluidStack stack, int capacity, int mouseX, int mouseY) {
        Component displayName;

        // Amount が 0 以下の場合は「空」として扱う
        if (stack.getAmount() <= 0) {
            displayName = Component.translatable("gui.ultimatecraftv2.empty");
        } else {
            displayName = stack.getDisplayName();
        }

        // 数字のフォーマット（カンマ区切り 10,000）を適用した文字列を作成
        String amountStr = String.format("%, d", stack.getAmount());
        String capacityStr = String.format("%, d", capacity);

        // 「液体名: 1,000 / 10,000 mB」の形式で表示
        Component tooltip = Component.literal("")
                .append(displayName)
                .append(": " + amountStr + " / " + capacityStr + " mB");

        guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
    }
}