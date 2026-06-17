package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.EthylenePlantMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EthylenePlantScreen extends AbstractContainerScreen<EthylenePlantMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/ethylene_plant.png");

    public EthylenePlantScreen(EthylenePlantMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

        // 1. ベースのメインGUI背景を描画
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. 新指定座標：FEバー（エネルギーゲージ）の動的描画
        // 座標 (41, 17) から終点 (43, 67) より、幅2px / 高さ50px
        int maxEnergyHeight = 50;
        int energyBarHeight = (int) ((long) this.menu.getEnergy() * maxEnergyHeight / this.menu.getMaxEnergy());

        if (energyBarHeight > 0) {
            // 下から上へエネルギー（色）が競り上がる描画計算
            graphics.blit(TEXTURE,
                    x + 41, y + 67 - energyBarHeight,
                    41, 67 - energyBarHeight,
                    2, energyBarHeight);
        }

        // 3. 燃料スロット(56, 53)の上の「炎マーク」のアニメーション描画
        // ※バニラ標準：燃料スロットのすぐ上 (56, 36) 付近にある炎のテクスチャ (通常は参照元 176, 0 / 幅14px / 高さ14px)
        if (this.menu.isBurning()) {
            int maxBurnHeight = 14;
            int burnScaled = this.menu.getBurnTime() * maxBurnHeight / this.menu.getMaxBurnTime();

            // 炎が上から下に向かって徐々に消えていく（薪が燃え尽きる）演出の計算
            graphics.blit(TEXTURE,
                    x + 57, y + 36 + (maxBurnHeight - burnScaled),
                    176, maxBurnHeight - burnScaled,
                    14, burnScaled);
        }

        // 4. 進行度矢印のアニメーション描画
        // 中央の灰色矢印の位置 (79, 35) に、右上の白矢印参照元 (176, 14) から幅22px分を徐々に上書き
        int maxProgressWidth = 22;
        int progress = this.menu.getProgress();
        int maxProgress = 200; // BlockEntity側で設定した 200 tick (10秒) に完全同期
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

        // 【ツールチップ表示】新指定のFEバー判定エリア (41, 17) 〜 (43, 67)
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x + 41 && mouseX <= x + 43 && mouseY >= y + 17 && mouseY <= y + 67) {
            Component text = Component.literal(this.menu.getEnergy() + " / " + this.menu.getMaxEnergy() + " FE");
            graphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }
}