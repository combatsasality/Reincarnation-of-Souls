package com.combatsasality.scol.packets.server;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.packets.client.PacketSetCapability;
import com.combatsasality.scol.registries.ScolCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketGetCapability {

    private final boolean rendered;

    public PacketGetCapability(boolean rendered) {
        this.rendered = rendered;
    }

    public static void encode(PacketGetCapability msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.rendered);
    }

    public static PacketGetCapability decode(FriendlyByteBuf buf) {
        return new PacketGetCapability(buf.readBoolean());
    }

    public static void handle(PacketGetCapability msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            Main.packetInstance.send(PacketDistributor.PLAYER.with(() -> player), new PacketSetCapability(player.getCapability(ScolCapabilities.SCOL_CAPABILITY).map(capa -> capa.writeTag()).orElse(null)));
        });
        ctx.get().setPacketHandled(true);
    }

}

