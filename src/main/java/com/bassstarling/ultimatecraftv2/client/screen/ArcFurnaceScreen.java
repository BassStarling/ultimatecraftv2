package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.ArcFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ArcFurnaceScreen extends AbstractContainerScreen<ArcFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/arc_furnace.png");

    public ArcFurnaceScreen(ArcFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 1. 背景テクスチャの描画
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. スパークエネルギーゲージの描画 (画像左端の細い棒)
        // 座標: x+21, y+16 から 高さ46px想定
        int bufferHeight = (int)((float)menu.data.get(0) / menu.data.get(1) * 46);
        if (bufferHeight > 0) {
            // 色: スパークらしい水色 (0xFF00E5FF)
            guiGraphics.fill(x + 50, y + 57 - bufferHeight, x + 53, y + 70, 0xFF00E5FF);
        }

        // 3. 進捗矢印の描画 (中央のグレー矢印に重ねる)
        int progressWidth = menu.getScaledProgress();
        if (progressWidth > 0) {
            // 矢印のテクスチャ位置 (176, 0) はテクスチャ画像の右側余白に描いておく必要があります
            guiGraphics.blit(TEXTURE, x + 79, y + 29, 176, 0, progressWidth, 17);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // エネルギーのツールチップ表示 (判定範囲を画像左端のゲージに修正)
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x + 50 && mouseX <= x + 53 && mouseY >= y + 16 && mouseY <= y + 62) {
            guiGraphics.renderTooltip(this.font,
                    Component.literal(menu.data.get(0) + " / " + menu.data.get(1) + " Spark"),
                    mouseX, mouseY);
        }
    }
}
