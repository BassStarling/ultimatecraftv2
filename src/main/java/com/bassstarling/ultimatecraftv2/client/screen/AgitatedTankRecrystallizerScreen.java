package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.C2SUpdateRecrystallizerParamPacket;
import com.bassstarling.ultimatecraftv2.menu.AgitatedTankRecrystallizerMenu;
import com.bassstarling.ultimatecraftv2.registry.ModMessages;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AgitatedTankRecrystallizerScreen extends AbstractContainerScreen<AgitatedTankRecrystallizerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("ultimatecraftv2", "textures/gui/recrystallizer.png");

    private EditBox flowEdit;
    private EditBox powerEdit;

    public AgitatedTankRecrystallizerScreen(AgitatedTankRecrystallizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 175; // 画像サイズに合わせて調整
    }

    @Override
    protected void init() {
        super.init();

        // 数値入力欄1 (Flow): 56, 57
        this.flowEdit = new EditBox(this.font, leftPos + 56, topPos + 57, 53, 8, Component.empty());
        this.flowEdit.setBordered(false); // 背景画像があるので枠は消す
        this.flowEdit.setMaxLength(3);
        this.flowEdit.setTextColor(0xFFFFFF);
        this.flowEdit.setValue(String.valueOf(menu.getUserInputFlow()));
        this.flowEdit.setResponder(s -> sendUpdatePacket()); // 値が変わるたびに送信
        this.addRenderableWidget(flowEdit);

        // 数値入力欄2 (Power): 56, 68
        this.powerEdit = new EditBox(this.font, leftPos + 56, topPos + 68, 53, 8, Component.empty());
        this.powerEdit.setBordered(false);
        this.powerEdit.setMaxLength(3);
        this.powerEdit.setTextColor(0xFFFFFF);
        this.powerEdit.setValue(String.valueOf(menu.getUserInputPower()));
        this.powerEdit.setResponder(s -> sendUpdatePacket());
        this.addRenderableWidget(powerEdit);
    }

    private void sendUpdatePacket() {
        try {
            int flow = Integer.parseInt(flowEdit.getValue());
            int power = Integer.parseInt(powerEdit.getValue());

            // パケットの送信（PacketHandlerはご自身の環境に合わせてください）
            ModMessages.sendToServer(new C2SUpdateRecrystallizerParamPacket(menu.getBlockEntity().getBlockPos(), flow, power));
        } catch (NumberFormatException ignored) {
            // 数字以外が入った場合は無視
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // デフォルトのタイトル表示を消すか、位置を調整
        // 数値表示1 (温度): 56, 16
        graphics.drawString(this.font, String.format("%.1f C", menu.getTemperature()), 58, 16, 0x00FF00, false);
        // 数値表示2 (効率): 56, 27
        graphics.drawString(this.font, String.format("EFF: %d%%", menu.getEfficiency()), 58, 27, 0x00FF00, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = leftPos;
        int y = topPos;

        // 背景描画
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 1. スパークバー (16, 15) サイズ 2x62
        int sparkHeight = menu.getSparkScaled(62);
        // テクスチャ内のスパーク色部分（例えば 176, 0 にあると仮定）を blit
        graphics.blit(TEXTURE, x + 16, y + 15 + (62 - sparkHeight), 176, 0, 2, sparkHeight);

        // 2. 進行度矢印 (79, 38)
        // 参照元 (176, 17) 幅 24（仮）高さ 17
        int progressWidth = menu.getProgressScaled(24);
        if (progressWidth > 0) {
            graphics.blit(TEXTURE, x + 79, y + 38, 176, 17, progressWidth, 17);
        }

        // 3. タンクの描画 (21, 15) と (112, 15)
        // ※流体の描画にはFluidStackのテクスチャ取得などの別途ユーティリティが必要
        renderFluidTank(graphics, x + 21, y + 15, menu.getInputFluid());
        renderFluidTank(graphics, x + 112, y + 15, menu.getOutputFluid());
    }

    private void renderFluidTank(GuiGraphics graphics, int x, int y, net.minecraftforge.fluids.FluidStack fluid) {
        if (fluid.isEmpty()) return;
        // ここでFluidRendererなどの自作クラスを使って流体を描画する
    }
}