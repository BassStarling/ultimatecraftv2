package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.DustCollectorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DustCollectorScreen extends AbstractContainerScreen<DustCollectorMenu> {
    // 作成されたテクスチャの場所を指定 (assets/ultimatecraftv2/textures/gui/dustcollector.png)
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/dustcollector.png");

    public DustCollectorScreen(DustCollectorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        // 画像全体のサイズ（テクスチャ画像の幅と高さが256x256の場合）
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // タイモ文字（Inventory や Dust Collector）の位置微調整
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 74; // インベントリの少し上に配置
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 1. ベースとなるGUI背景（dustcollector.png）をそのまま描画
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. FEバー（エネルギーゲージ）の動的描画
        // 残量に応じて高さを計算 (最大51px)
        int maxEnergyHeight = 51;
        int storedEnergy = this.menu.getEnergy();
        int maxEnergy = this.menu.getMaxEnergy();
        int energyBarHeight = maxEnergy > 0 ? (int) ((long) storedEnergy * maxEnergyHeight / maxEnergy) : 0;

        if (energyBarHeight > 0) {
            // 下から上に伸びるように、描画開始のY座標をずらしてカット描画する
            // 引数: (画像, 画面X, 画面Y, テクスチャ内のX, テクスチャ内のY, 幅, 高さ)
            graphics.blit(TEXTURE,
                    x + 50, y + 68 - energyBarHeight,
                    50, 68 - energyBarHeight,
                    2, energyBarHeight);
        }

        // 3. 進行度矢印（白い矢印を灰色の上に重ねる）のアニメーション描画
        // いただいた座標から、灰色矢印のサイズを 幅22px、高さ16px と仮定
        int maxProgressWidth = 22;
        int progress = this.menu.getProgress();
        int maxProgress = 40; // BlockEntity側で設定した2秒(40tick)
        int arrowWidth = maxProgress > 0 ? (progress * maxProgressWidth / maxProgress) : 0;

        if (arrowWidth > 0) {
            // 右上の白矢印参照元(176, 14)から必要な長さだけ切り出し、中央の灰色矢印(79, 35)の上に重ねる
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

        // 【おまけ機能】FEバーにマウスを合わせた時に「〇〇 / 36000 FE」とツールチップを出す
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x + 50 && mouseX <= x + 52 && mouseY >= y + 17 && mouseY <= y + 68) {
            Component text = Component.literal(this.menu.getEnergy() + " / " + this.menu.getMaxEnergy() + " FE");
            graphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }
}