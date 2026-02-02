package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.CastingMachineMenu;
import com.bassstarling.ultimatecraftv2.menu.ElectrolyticFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CastingMachineScreen extends AbstractContainerScreen<CastingMachineMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2",
                    "textures/gui/electrolytic_furnace.png");

    public CastingMachineScreen(
            CastingMachineMenu menu,
            Inventory playerInv,
            Component title
    ) {
        super(menu, playerInv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private int getProgressArrowScale() {
        int progress = menu.getProgress();
        int max = menu.getMaxProgress();

        if (max <= 0 || progress <= 0) return 0;

        return progress * 24 / max; // 24 = 矢印の最大幅
    }

    /* ===== タイトル描画 ===== */
    @Override
    protected void renderLabels(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        guiGraphics.drawString(
                font,
                title,
                8,
                6,
                4210752,
                false
        );

        guiGraphics.drawString(
                font,
                playerInventoryTitle,
                8,
                imageHeight - 96 + 2,
                4210752,
                false
        );
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        // 背景の描画
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 進捗矢印の描画（かまどと同様の仕組み）
        if (menu.getScaledProgress() > 0) {
            // blit(テクスチャ, 表示先X, 表示先Y, 素材のX, 素材のY, 幅, 高さ)
            // 素材のYを172などにしているのは、元のテクスチャの下の方に「色付き矢印」を隠している想定です
            guiGraphics.blit(TEXTURE, x + 79, y + 26, 176, 0, menu.getScaledProgress(), 17);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
