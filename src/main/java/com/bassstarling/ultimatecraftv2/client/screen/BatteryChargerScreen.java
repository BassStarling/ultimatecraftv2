package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.BatteryChargerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BatteryChargerScreen extends AbstractContainerScreen<BatteryChargerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/batterycharger.png");

    public BatteryChargerScreen(BatteryChargerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // --- アーク炉準拠：エネルギーのツールチップ表示 ---
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // バーの判定範囲 (X: 74~76, Y: 17~68)
        if (mouseX >= x + 74 && mouseX <= x + 76 && mouseY >= y + 17 && mouseY <= y + 69) {
            // BlockEntity側で10で割って送信しているため、表示用に10倍して元の値に戻す
            int displayEnergy = menu.getEnergy() * 10;
            // 最大値は BatteryChargerBlockEntity で定義した 36000
            int maxEnergy = 36000;

            guiGraphics.renderTooltip(this.font,
                    Component.literal(displayEnergy + " / " + maxEnergy + " Spark"),
                    mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 背景テクスチャの描画
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // --- アーク炉準拠：スパークエネルギーゲージの描画 ---
        int energy = menu.getEnergy();
        int maxEnergy = 3600; // 10分の1のスケールでの最大値
        int barFullHeight = 51; // 高さ (68 - 17 = 51)

        // 割合計算
        int bufferHeight = (energy > 0) ? (int)(((float)energy / maxEnergy) * barFullHeight) : 0;
        // 安全装置
        if (bufferHeight > barFullHeight) bufferHeight = barFullHeight;

        if (bufferHeight > 0) {
            // アーク炉と同じスパークカラー (0xFF00E5FF) で塗りつぶし
            // fill(startX, startY, endX, endY, color)
            // Xは 74から77(幅3px), Yは下(68)から上(68-bufferHeight)へ伸びる
            guiGraphics.fill(x + 74, y + 68 - bufferHeight, x + 77, y + 68, 0xFF00E5FF);
        }
    }
}