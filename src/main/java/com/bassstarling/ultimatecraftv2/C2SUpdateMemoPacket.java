package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.item.MemoItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SUpdateMemoPacket {
    private final String text;
    private final InteractionHand hand;

    public C2SUpdateMemoPacket(String text, InteractionHand hand) {
        this.text = text;
        this.hand = hand;
    }

    public C2SUpdateMemoPacket(FriendlyByteBuf buf) {
        this.text = buf.readUtf();
        this.hand = buf.readEnum(InteractionHand.class);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(text);
        buf.writeEnum(hand);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                // プレイヤーが現在メモ帳を開いた方の手（メイン/オフ）のアイテムを取得
                ItemStack stack = player.getItemInHand(hand);
                if (!stack.isEmpty() && stack.getItem() instanceof MemoItem) {
                    // サーバー側でNBTに文字列を書き込む
                    stack.getOrCreateTag().putString("MemoText", text);
                }
            }
        });
        return true;
    }
}