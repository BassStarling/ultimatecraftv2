package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.UltimateCraftV2;
import com.bassstarling.ultimatecraftv2.menu.IndustrialWorkbenchMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IndustrialWorkbenchScreen extends AbstractContainerScreen<IndustrialWorkbenchMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(UltimateCraftV2.MOD_ID, "textures/gui/industrial_workbench.png");

    public IndustrialWorkbenchScreen(IndustrialWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        // 画像全体の幅(256)の中で、右側のリザルトまでゆとりを持って表示
        this.imageWidth = 212;
        this.imageHeight = 220;

        // 「業務用作業台」の位置 (5x5の左端 startX=30 に合わせる)
        this.titleLabelX = 30;

        // 「インベントリ」の位置 (インベントリの左端 X=26 に合わせる)
        // Y座標を少し下げて、枠線と文字が重ならないようにします
        this.inventoryLabelX = 26;
        this.inventoryLabelY = 110;
    }

    @Override
    protected void init() {
        super.init();
        // 必要ならここにボタンの追加などを行う（今回は空でもOK）
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // 1. 背景の暗転（これが無いと画面がバグったように見えることがあります）
        this.renderBackground(guiGraphics);

        // 2. 本体の描画
        super.render(guiGraphics, mouseX, mouseY, delta);

        // 3. ツールチップ（アイテムにマウスを乗せた時の説明）の描画
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // RenderSystemの設定は1.20.1ではGuiGraphicsが内部でやってくれるので、最小限でOK
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 背景テクスチャの描画
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
}
