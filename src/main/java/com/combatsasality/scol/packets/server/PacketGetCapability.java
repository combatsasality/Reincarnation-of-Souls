package com.combatsasality.scol.packets.server;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.ScolCapabality;
import com.combatsasality.scol.packets.client.PacketCapa;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketGetCapability {

    private final boolean rendered;

    public PacketGetCapability(boolean rendered) {this.rendered = rendered;}

    public static void encode(PacketGetCapability msg, PacketBuffer buf) {buf.writeBoolean(msg.rendered);}

    public static PacketGetCapability decode(PacketBuffer buf) {return new PacketGetCapability(buf.readBoolean());}

    public static void handle(PacketGetCapability msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity playerServ = ctx.get().getSender();
            Main.packetInstance.send(PacketDistributor.PLAYER.with(() -> playerServ), new PacketCapa(playerServ.getCapability(ScolCapabality.NeedVariables).map(capa -> capa.getNBT()).orElse(null)));
        });
        ctx.get().setPacketHandled(true);
    }
}
