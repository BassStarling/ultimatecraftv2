package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.OxidizerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OxidizerScreen extends AbstractContainerScreen<OxidizerMenu> {
    // assets/ultimatecraftv2/textures/gui/oxidizer.png を参照
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/oxidizer.png");

    public OxidizerScreen(OxidizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // タイトルやインベントリ文字の配置（標準位置）
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

        // 1. メインのGUI背景テクスチャを描画
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. ご提示いただいた座標に基く「進行度矢印」の動的アニメーション描画
        // 配置座標 (79, 35) / 参照元 (176, 14) / 矢印の最大幅 22px / 高さ 16px
        int maxProgressWidth = 22;
        int progress = this.menu.getProgress();
        int maxProgress = 800; // BlockEntity側で設定した 40秒 (800 tick) に完全同期

        // 進捗割合から、現在描画すべき矢印の横幅を計算
        int arrowWidth = maxProgress > 0 ? (progress * maxProgressWidth / maxProgress) : 0;

        if (arrowWidth > 0) {
            graphics.blit(TEXTURE,
                    x + 79, y + 35,       // 画面上の描画開始位置
                    176, 14,              // テクスチャ内の参照元座標
                    arrowWidth, 16);      // 計算された幅と、標準の高さ16px
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }
}