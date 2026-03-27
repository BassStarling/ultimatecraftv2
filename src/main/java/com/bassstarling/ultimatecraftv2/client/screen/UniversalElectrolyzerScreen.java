package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.UniversalElectrolyzerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class UniversalElectrolyzerScreen extends AbstractContainerScreen<UniversalElectrolyzerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2", "textures/gui/universal_electrolyzer.png");

    public UniversalElectrolyzerScreen(UniversalElectrolyzerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 172;
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // GuiGraphics経由で描画
        int x = leftPos;
        int y = topPos;

        // 背景の描画
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // エネルギーバーの描画 (16, 15)
        renderEnergyBar(guiGraphics, x + 16, y + 15);

        // プログレスバー（白色矢印）の描画 (79, 38)
        if (menu.isCrafting()) {
            int progressWidth = menu.getScaledProgress(24);
            guiGraphics.blit(TEXTURE, x + 79, y + 38, 176, 17, progressWidth, 17);
        }

        // タンクの描画
        renderFluid(guiGraphics, menu.getInputFluid(), x + 21, y + 15, 32, 62);
        renderFluid(guiGraphics, menu.getOutputFluid(), x + 112, y + 15, 32, 62);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energyStored = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        int barHeight = (maxEnergy != 0 && energyStored != 0) ? (int) ((float) energyStored / maxEnergy * 62) : 0;

        if (barHeight > 0) {
            guiGraphics.blit(TEXTURE, x, y + (62 - barHeight), 176, 0, 2, barHeight);
        }
    }

    private void renderFluid(GuiGraphics guiGraphics, FluidStack fluid, int x, int y, int width, int height) {
        if (fluid.isEmpty()) return;

        // 流体のテクスチャ (Sprite) を取得
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTexture = props.getStillTexture(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

        // 流体の色を取得 (RGBA)
        int color = props.getTintColor(fluid);
        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;

        // 描画する高さを計算 (最大10000mBで62px)
        int fluidHeight = (int) (height * (fluid.getAmount() / 10000f));
        if (fluidHeight < 1 && fluid.getAmount() > 0) fluidHeight = 1; // 少量でも1pxは表示
        if (fluidHeight > height) fluidHeight = height;

        // 描画設定（色を適用）
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        // 描画 (下から上に溜まるように y 座標を調整)
        // タンクのサイズに合わせてテクスチャをタイル状に描画
        int renderY = y + (height - fluidHeight);

        // guiGraphics.blit(x, y, z, width, height, sprite)
        // 32x62の範囲に流体を描画
        guiGraphics.blit(x, renderY, 0, width, fluidHeight, sprite);

        // 描画後は色設定を白に戻す（重要：戻さないと他のGUIも流体の色に染まります）
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // タイトルなどを描画（必要であれば）
        super.renderLabels(guiGraphics, mouseX, mouseY);

        // タンクの範囲内にマウスがあるかチェック
        // 入力タンク (21, 15) ～ (53, 77)
        if (isHovering(21, 15, 32, 62, mouseX, mouseY)) {
            renderFluidTooltip(guiGraphics, menu.getInputFluid(), mouseX - leftPos, mouseY - topPos);
        }
        // 出力タンク (112, 15) ～ (144, 77)
        if (isHovering(112, 15, 32, 62, mouseX, mouseY)) {
            renderFluidTooltip(guiGraphics, menu.getOutputFluid(), mouseX - leftPos, mouseY - topPos);
        }
    }

    private void renderFluidTooltip(GuiGraphics guiGraphics, FluidStack fluid, int x, int y) {
        List<Component> tooltip = new ArrayList<>();
        if (fluid.isEmpty()) {
            tooltip.add(Component.translatable("gui.ultimatecraftv2.empty"));
        } else {
            tooltip.add(fluid.getDisplayName());
            tooltip.add(Component.literal(fluid.getAmount() + " / 10000 mB").withStyle(ChatFormatting.GRAY));
        }
        guiGraphics.renderComponentTooltip(font, tooltip, x, y);
    }
}