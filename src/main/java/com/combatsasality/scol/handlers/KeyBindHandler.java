package com.combatsasality.scol.handlers;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.packets.server.PacketWorldWing;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class KeyBindHandler {
    public KeyMapping worldWingKeyBind;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START
                || !Minecraft.getInstance().isWindowActive()
                || Minecraft.getInstance().player == null) return;

        if (this.worldWingKeyBind.consumeClick()) {
            Main.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketWorldWing(true));
        }
    }
}
