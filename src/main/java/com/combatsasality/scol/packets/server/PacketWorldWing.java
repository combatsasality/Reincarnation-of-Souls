package com.combatsasality.scol.packets.server;

import com.combatsasality.scol.items.WorldWing;
import com.combatsasality.scol.registries.ScolItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class PacketWorldWing {
    private final boolean pressed;

    public PacketWorldWing(boolean pressed) {
        this.pressed = pressed;
    }

    public static void encode(PacketWorldWing msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.pressed);
    }

    public static PacketWorldWing decode(FriendlyByteBuf buf) {
        return new PacketWorldWing(buf.readBoolean());
    }

    public static void handle(PacketWorldWing msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                inventory.findFirstCurio(ScolItems.WORLD_WING).ifPresent(curio -> {
                    ItemStack stack = curio.stack();
                    if (WorldWing.getFlySpeedInt(stack) == 4) {
                        WorldWing.setFlySpeed(stack, 0);
                    } else {
                        WorldWing.setFlySpeed(stack, WorldWing.getFlySpeedInt(stack) + 1);
                    }
                    player.sendSystemMessage(Component.translatable("chat.send.wingspeed", WorldWing.getFlySpeedInt(stack)), true);
                });
            });
        });

        ctx.get().setPacketHandled(true);
    }


}
