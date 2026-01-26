package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.CokeOvenMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.bassstarling.ultimatecraftv2.UltimateCraftV2.MOD_ID;

public class CokeOvenScreen extends AbstractContainerScreen<CokeOvenMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/coke_oven.png");

    public CokeOvenScreen(CokeOvenMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);



        // --- 液体ゲージ (右側の細長い枠) ---
        int fluidHeight = menu.getScaledFluid(); // 最大値はゲージの高さに合わせて調整（例：52px）
        if (fluidHeight > 0) {
            // x座標: バケツスロット(116+18)のすぐ右 = 136付近
            // y座標: ゲージの下端を 70 と仮定
            // 引数: x1, y1, x2, y2, color
            guiGraphics.fill(x + 139, y + 66 - fluidHeight, x + 142, y + 68, 0xFF111111);
        }

        // 進捗ゲージ（矢印）の描画
        // getScaledProgress() をここで「使用」します
        int progressWidth = menu.getScaledProgress();
        if (progressWidth > 0) {
            // 矢印の座標が x+79, y+34 付近にあると仮定
            // 引数: Texture, 画面X, 画面Y, テクスチャ内のX, テクスチャ内のY, 幅, 高さ
            guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 14, progressWidth, 17);
        }

        // --- 燃焼ゲージ (中央の煙/炎アイコン) ---
        if (menu.isBurning()) {
            int burnHeight = menu.getBurnProgress();
            // 炎のアイコンが 56, 36 付近にある場合
            guiGraphics.blit(TEXTURE, x + 56, y + 36 + (12 - burnHeight), 176, 12 - burnHeight, 14, burnHeight + 1);
        }
    }

    // CokeOvenScreen.java 内

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // 1. 背景を暗くする（バニラの標準的な挙動）
        this.renderBackground(guiGraphics);

        // 2. 親クラスの描画（背景画像やスロット内のアイテムを描画）
        super.render(guiGraphics, mouseX, mouseY, delta);

        // 3. 重要：マウスが載っているアイテムのツールチップ（名前や情報）を描画
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}