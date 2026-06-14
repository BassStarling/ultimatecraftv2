package com.bassstarling.ultimatecraftv2.client.screen;

import com.bassstarling.ultimatecraftv2.C2SUpdateMemoPacket;
import com.bassstarling.ultimatecraftv2.registry.ModMessages;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class MemoScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("ultimatecraftv2", "textures/gui/memo.png");

    private final ItemStack memoStack;
    private final InteractionHand hand;
    private MultiLineEditBox editBox;

    private int imageWidth = 200;
    private int imageHeight = 200;
    private int leftPos;
    private int topPos;

    public MemoScreen(ItemStack memoStack, InteractionHand hand) {
        super(Component.literal("Memo Pad"));
        this.memoStack = memoStack;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // 複数行入力ボックスの配置
        this.editBox = new MultiLineEditBox(this.font, leftPos + 15, topPos + 40, 170, 120, Component.empty(), Component.empty());

        this.editBox.setValueListener(s -> {
            if (s.length() > 512) {
                this.editBox.setValue(s.substring(0, 512));
            }
        });

        if (memoStack.hasTag() && memoStack.getTag().contains("MemoText")) {
            this.editBox.setValue(memoStack.getTag().getString("MemoText"));
        }
        this.addRenderableWidget(this.editBox);

        // ボタン類（略）...
        this.addRenderableWidget(Button.builder(Component.literal("赤"), b -> applyColorToSelection("§c"))
                .bounds(leftPos + 15, topPos + 15, 30, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("青"), b -> applyColorToSelection("§9"))
                .bounds(leftPos + 50, topPos + 15, 30, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("緑"), b -> applyColorToSelection("§a"))
                .bounds(leftPos + 85, topPos + 15, 30, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("保存"), b -> saveAndClose())
                .bounds(leftPos + 135, topPos + 165, 50, 20).build());
        // 黒/白の標準色に戻すための「戻す」ボタン（§r を挿入する）
        this.addRenderableWidget(Button.builder(Component.literal("戻す"), b -> applyColorToSelection("§r"))
                .bounds(leftPos + 120, topPos + 15, 30, 20).build());
    }

    private void applyColorToSelection(String colorCode) {
        String currentText = this.editBox.getValue();

        // 512文字の制限を超えないかチェック
        if (currentText.length() + 4 > 512) {
            return;
        }

        // 豆知識：もし将来的にカーソル位置への挿入を厳密に行いたい場合は、
        // Forgeの「AccessTransformer」を使って MultiLineEditBox 内の
        // `private final MultiLineEditBox.TextFieldHelper textFieldHelper` を公開する必要がありますが、
        // まずはこの「末尾追加」方式で安全に動かすのがおすすめです。
        this.editBox.setValue(currentText + colorCode);
    }

    private void saveAndClose() {
        String finalOutput = this.editBox.getValue();

        // パケットをサーバーへ送り、手持ちのアイテムのNBTに書き込む
        ModMessages.sendToServer(new C2SUpdateMemoPacket(finalOutput, this.hand));

        // 画面を閉じるバニラのメソッド
        this.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);

        // 背景画像の描画
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        super.render(graphics, mouseX, mouseY, delta);
    }
}