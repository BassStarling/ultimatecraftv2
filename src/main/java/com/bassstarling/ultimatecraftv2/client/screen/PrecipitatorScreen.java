package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.blockentity.PrecipitatorBlockEntity;
import com.bassstarling.ultimatecraftv2.menu.PrecipitatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class PrecipitatorScreen extends AbstractContainerScreen<PrecipitatorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/precipitator.png");

    public PrecipitatorScreen(PrecipitatorMenu menu, Inventory inventory, Component title) {
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

        // 背景描画 (256x256テクスチャ)
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        PrecipitatorBlockEntity be = menu.getBlockEntity();

        // --- 1. 液体タンクの描画 ---

        // 入力タンク (16, 15) -> 終点 (52, 75) | 幅36, 高さ60
        renderFluidBar(guiGraphics, be.getInputTank(), x + 16, y + 15, 36, 60);

        // 出力タンク (112, 15) -> 終点 (148, 76) | 幅36, 高さ61
        renderFluidBar(guiGraphics, be.getOutputTank(), x + 112, y + 15, 36, 61);

        // --- 2. 進行矢印の描画 (61, 38) ---
        // 灰色矢印は背景に含まれている前提で、上から白矢印を重ねる
        if (this.menu.isProcessing()) {
            // getScaledProgressで計算された幅(最大24px)で blit
            // テクスチャ座標 (176, 17) から白矢印を取得
            int progressWidth = this.menu.getScaledProgress();
            guiGraphics.blit(TEXTURE, x + 61, y + 38, 176, 17, progressWidth, 17);
        }
    }

    /**
     * 汎用的な液体バー描画メソッド
     */
    private void renderFluidBar(GuiGraphics guiGraphics, FluidTank tank, int x, int y, int width, int height) {
        if (tank.isEmpty()) return;

        int amount = tank.getFluidAmount();
        int capacity = tank.getCapacity();
        int color = getFluidColor(tank.getFluid(), 0xFFFFFFFF);

        int fluidHeight = (int) ((float) amount / capacity * height);

        // fill(x1, y1, x2, y2, color) で描画
        guiGraphics.fill(x, y + height - fluidHeight, x + width, y + height, color);
    }

    private int getFluidColor(FluidStack stack, int defaultColor) {
        if (stack.isEmpty()) return defaultColor;
        int color = IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack);
        return color == 0 ? defaultColor : color;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // ツールチップの描画
        renderTankTooltips(guiGraphics, mouseX, mouseY);
    }

    /**
     * 各タンクの座標に基づいたツールチップ判定
     */
    private void renderTankTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 入力タンク判定 (16, 15) ～ (52, 75)
        if (isMouseOver(mouseX, mouseY, x + 16, y + 15, 36, 60)) {
            renderFluidTooltip(guiGraphics, menu.getBlockEntity().getInputTank(), mouseX, mouseY);
        }

        // 出力タンク判定 (112, 15) ～ (148, 76)
        if (isMouseOver(mouseX, mouseY, x + 112, y + 15, 36, 61)) {
            renderFluidTooltip(guiGraphics, menu.getBlockEntity().getOutputTank(), mouseX, mouseY);
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void renderFluidTooltip(GuiGraphics guiGraphics, FluidTank tank, int mouseX, int mouseY) {
        Component displayName = tank.isEmpty() ?
                Component.translatable("gui.ultimatecraftv2.empty") : tank.getFluid().getDisplayName();

        String amountStr = String.format("%,d", tank.getFluidAmount());
        String capacityStr = String.format("%,d", tank.getCapacity());

        Component tooltip = Component.literal("")
                .append(displayName)
                .append(": " + amountStr + " / " + capacityStr + " mB");

        guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
    }
}