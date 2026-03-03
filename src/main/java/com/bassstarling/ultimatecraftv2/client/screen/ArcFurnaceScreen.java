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

        // 背景テクスチャの描画
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // スパークエネルギーゲージの描画
        int bufferHeight = (int)((float)menu.data.get(0) / menu.data.get(1) * 46);
        if (bufferHeight > 0) {
            // 色: スパークらしい水色 (0xFF00E5FF)
            guiGraphics.fill(x + 50, y + 57 - bufferHeight, x + 53, y + 69, 0xFF00E5FF);
        }

        // 進捗矢印
        int progressWidth = menu.getScaledProgress();
        if (progressWidth > 0) {
            guiGraphics.blit(TEXTURE, x + 79, y + 33, 176, 12, progressWidth, 17);
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
