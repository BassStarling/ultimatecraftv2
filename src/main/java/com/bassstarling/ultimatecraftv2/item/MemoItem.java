package com.bassstarling.ultimatecraftv2.item;

import com.bassstarling.ultimatecraftv2.client.screen.MemoScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Properties;

public class MemoItem extends Item {
    public MemoItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        // クライアント側（画面が描画できる側）でのみGUIを開く
        if (pLevel.isClientSide) {
            // 直接 Minecraft の画面表示機能を使って MemoScreen を開く
            net.minecraft.client.Minecraft.getInstance().setScreen(new MemoScreen(itemstack, pHand));
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
    }

    // アイテムにカーソルを合わせた時（ツールチップ）に、メモの内容をプレビュー表示する
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pStack.hasTag() && pStack.getTag().contains("MemoText")) {
            String text = pStack.getTag().getString("MemoText");
            pTooltipComponents.add(Component.literal("【内容】"));

            // 改行コード(\n)で文章を分割して、1行ずつツールチップに追加する
            String[] lines = text.split("\n");
            for (String line : lines) {
                // Forge/Minecraft標準の「§による色コード」を内部的にComponent構造にパースするメソッド
                pTooltipComponents.add(Component.literal(line));
            }
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.ultimatecraftv2.memo.empty"));
        }
    }
}