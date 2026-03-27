package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.blockentity.FilterBlockEntity;
import com.bassstarling.ultimatecraftv2.menu.FilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FilterScreen extends AbstractContainerScreen<FilterMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/filter.png");

    public FilterScreen(FilterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 170; // インベントリ座標 (90, 148) に合わせた高さ
        this.titleLabelY = 3;
        this.inventoryLabelY = 78;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 背景描画
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // --- 液体タンクの描画 (3箇所) ---
        FilterBlockEntity be = menu.getBlockEntity();

        // タンク1: 入力 (16, 15)
        renderFluidBar(guiGraphics, be.getInputTank(), x + 16, y + 15);

        // タンク2: 赤い泥 (84, 15)
        renderFluidBar(guiGraphics, be.getRedMudTank(), x + 84, y + 15);

        // タンク3: 濾過液 (126, 15)
        renderFluidBar(guiGraphics, be.getPureAluminateTank(), x + 126, y + 15);

        // --- 矢印の描画 (57, 38) ---
        // 進行度バー（白い矢印）: テクスチャ上の座標 (176, 17) から切り出し
        if (this.menu.isFiltering()) {
            int progressWidth = this.menu.getScaledProgress(24);
            guiGraphics.blit(TEXTURE, x + 57, y + 38, 176, 17, progressWidth, 17);
        }
    }

    /**
     * DigesterScreen のロジックを継承し、FluidTank を直接渡せるように調整
     */
    private void renderFluidBar(GuiGraphics guiGraphics, FluidTank tank, int x, int y) {
        if (tank.isEmpty()) return;

        int amount = tank.getFluidAmount();
        int capacity = tank.getCapacity();
        int color = getFluidColor(tank.getFluid(), 0xFFFFFFFF);

        int tankHeight = 61; // 76 - 15 = 61
        int tankWidth = 36;  // 52 - 16 = 36
        int fluidHeight = (int) ((float) amount / capacity * tankHeight);

        // 液体を描画
        guiGraphics.fill(x, y + tankHeight - fluidHeight, x + tankWidth, y + tankHeight, color);
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
        renderTankTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderTankTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 3つのタンクそれぞれの判定
        if (isMouseOver(mouseX, mouseY, x + 16, y + 15, 36, 61))
            renderFluidTooltip(guiGraphics, menu.getBlockEntity().getInputTank(), mouseX, mouseY);

        if (isMouseOver(mouseX, mouseY, x + 84, y + 15, 36, 61))
            renderFluidTooltip(guiGraphics, menu.getBlockEntity().getRedMudTank(), mouseX, mouseY);

        if (isMouseOver(mouseX, mouseY, x + 126, y + 15, 36, 61))
            renderFluidTooltip(guiGraphics, menu.getBlockEntity().getPureAluminateTank(), mouseX, mouseY);
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void renderFluidTooltip(GuiGraphics guiGraphics, FluidTank tank, int mouseX, int mouseY) {
        Component displayName = tank.isEmpty() ?
                Component.translatable("gui.ultimatecraftv2.empty") : tank.getFluid().getDisplayName();

        String amountStr = String.format("%, d", tank.getFluidAmount());
        String capacityStr = String.format("%, d", tank.getCapacity());

        Component tooltip = Component.literal("").append(displayName).append(": " + amountStr + " / " + capacityStr + " mB");
        guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
    }
}