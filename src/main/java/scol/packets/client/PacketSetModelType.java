package scol.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import scol.entity.Onryo;

import java.util.function.Supplier;

public class PacketSetModelType {
    private int entityID;
    private int onryoType;

    public PacketSetModelType(int entityID, int onryoType) {
        this.entityID = entityID;
        this.onryoType = onryoType;
    }

    public static void encode(PacketSetModelType msg, PacketBuffer buf) {
        buf.writeInt(msg.entityID);
        buf.writeInt(msg.onryoType);
    }
    public static PacketSetModelType decode(PacketBuffer buf) {
        return new PacketSetModelType(buf.readInt(), buf.readInt());
    }

    public static void handle(PacketSetModelType msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(msg.entityID);
            if (entity.getClass().equals(Onryo.class)) {
                ((Onryo) entity).setOnryoType(msg.onryoType);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
