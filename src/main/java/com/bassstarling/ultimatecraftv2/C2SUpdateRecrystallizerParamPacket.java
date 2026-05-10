package com.bassstarling.ultimatecraftv2;

import com.bassstarling.ultimatecraftv2.blockentity.AgitatedTankRecrystallizerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SUpdateRecrystallizerParamPacket {
    private final BlockPos pos;
    private final int flow;
    private final int power;

    public C2SUpdateRecrystallizerParamPacket(BlockPos pos, int flow, int power) {
        this.pos = pos;
        this.flow = flow;
        this.power = power;
    }

    // ネットワーク経由で読み書きするためのコンストラクタ
    public C2SUpdateRecrystallizerParamPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.flow = buf.readInt();
        this.power = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(flow);
        buf.writeInt(power);
    }

    // サーバー側での受信処理
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 受信したサーバー側のワールドでBlockEntityを特定
            ServerPlayer player = context.getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(pos) instanceof AgitatedTankRecrystallizerBlockEntity be) {
                    // BlockEntityの数値を更新
                    be.setUserInputFlow(flow);
                    be.setUserInputPower(power);
                }
            }
        });
        return true;
    }
}