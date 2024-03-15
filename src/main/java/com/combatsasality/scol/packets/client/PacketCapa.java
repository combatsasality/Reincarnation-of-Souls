package com.combatsasality.scol.packets.client;

import com.combatsasality.scol.ScolCapabality;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCapa {
    private CompoundNBT nbt;

    public PacketCapa(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public static void encode(PacketCapa msg, PacketBuffer buf) {
        buf.writeNbt(msg.nbt);
    }
    public static PacketCapa decode(PacketBuffer buf) {
        return new PacketCapa(buf.readNbt());
    }

    public static void handle(PacketCapa msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            player.getCapability(ScolCapabality.NeedVariables).ifPresent(cap -> {
                cap.setNBT(msg.nbt);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
