package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.menu.ElectrolyticFurnaceMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectrolyticFurnaceScreen extends AbstractContainerScreen<ElectrolyticFurnaceMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ultimatecraftv2",
                    "textures/gui/electrolytic_furnace.png");

    public ElectrolyticFurnaceScreen(
            ElectrolyticFurnaceMenu menu,
            Inventory playerInv,
            Component title
    ) {
        super(menu, playerInv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 矢印（進捗）
        if (menu.isProcessing()) {
            int progress = menu.getScaledProgress(24);
            guiGraphics.blit(
                    TEXTURE,
                    x + 79,
                    y + 34,
                    176,
                    14,
                    progress,
                    17
            );
        }
    }

    /* ===== タイトル描画 ===== */
    @Override
    protected void renderLabels(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        guiGraphics.drawString(
                font,
                title,
                8,
                6,
                4210752,
                false
        );

        guiGraphics.drawString(
                font,
                playerInventoryTitle,
                8,
                imageHeight - 96 + 2,
                4210752,
                false
        );
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
