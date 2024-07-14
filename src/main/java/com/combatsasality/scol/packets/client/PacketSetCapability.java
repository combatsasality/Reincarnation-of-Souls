package com.combatsasality.scol.packets.client;

import com.combatsasality.scol.registries.ScolCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetCapability {
    private Tag tag;

    public PacketSetCapability(Tag tag) {
        this.tag = tag;
    }

    public static void encode(PacketSetCapability msg, FriendlyByteBuf buf) {
        buf.writeNbt((CompoundTag) msg.tag);
    }

    public static PacketSetCapability decode(FriendlyByteBuf buf) {
        return new PacketSetCapability(buf.readNbt());
    }

    public static void handle(PacketSetCapability msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            player.getCapability(ScolCapabilities.SCOL_CAPABILITY).ifPresent(cap -> {
                cap.readTag(msg.tag);
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
