package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.SinteringFurnaceMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SinteringFurnaceScreen extends AbstractContainerScreen<SinteringFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/sinteringfurnace.png");

    public SinteringFurnaceScreen(SinteringFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 1. 背景テクスチャの描画
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. FEバーの描画 (高さ 50px: 67 - 17)
        int maxEnergyHeight = 50;
        int energyBarHeight = (int) ((long) this.menu.getEnergy() * maxEnergyHeight / this.menu.getMaxEnergy());
        if (energyBarHeight > 0) {
            graphics.blit(TEXTURE,
                    x + 41, y + 67 - energyBarHeight,
                    41, 67 - energyBarHeight,
                    2, energyBarHeight);
        }

        // 3. かまどの炎のアニメーション描画 (最大高さ 14px と仮定)
        if (this.menu.isBurning()) {
            int maxBurnHeight = 14;
            int burnTimeLeft = this.menu.getBurnTime();
            int maxBurnTime = this.menu.getMaxBurnTime();
            int flameHeight = maxBurnTime > 0 ? (burnTimeLeft * maxBurnHeight / maxBurnTime) : 0;

            if (flameHeight > 0) {
                // 炎は下から上へ向かって消費（短く）されていくため、Y座標を補正して描画
                graphics.blit(TEXTURE,
                        x + 57, y + 37 + (maxBurnHeight - flameHeight),
                        176, maxBurnHeight - flameHeight,
                        14, flameHeight);
            }
        }

        // 4. 進捗矢印のアニメーション描画 (最大幅 22px)
        int maxProgressWidth = 22;
        int progress = this.menu.getProgress();
        int maxProgress = 200; // BlockEntity側で設定した10秒(200tick)
        int arrowWidth = maxProgress > 0 ? (progress * maxProgressWidth / maxProgress) : 0;

        if (arrowWidth > 0) {
            graphics.blit(TEXTURE,
                    x + 79, y + 35,
                    176, 14,
                    arrowWidth, 16);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);

        // FEバーのツールチップ表示
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x + 41 && mouseX <= x + 43 && mouseY >= y + 17 && mouseY <= y + 67) {
            Component text = Component.literal(this.menu.getEnergy() + " / " + this.menu.getMaxEnergy() + " FE");
            graphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }
}