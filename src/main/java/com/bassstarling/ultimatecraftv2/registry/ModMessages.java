package com.bassstarling.ultimatecraftv2.registry;

import com.bassstarling.ultimatecraftv2.C2SUpdateMemoPacket;
import com.bassstarling.ultimatecraftv2.C2SUpdateRecrystallizerParamPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation("ultimatecraftv2", "messages")) // ModIDに合わせて変更
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // パケットの登録
        net.messageBuilder(C2SUpdateRecrystallizerParamPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SUpdateRecrystallizerParamPacket::new)
                .encoder(C2SUpdateRecrystallizerParamPacket::toBytes)
                .consumerMainThread(C2SUpdateRecrystallizerParamPacket::handle)
                .add();

        net.messageBuilder(C2SUpdateMemoPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SUpdateMemoPacket::new)
                .encoder(C2SUpdateMemoPacket::toBytes)
                .consumerMainThread(C2SUpdateMemoPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}