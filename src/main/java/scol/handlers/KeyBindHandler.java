package scol.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import scol.Main;
import scol.items.WorldWing;
import scol.packets.server.PacketWorldWing;
import scol.registries.ScolItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class KeyBindHandler {
    public KeyBinding worldWingKeyBind;

    @OnlyIn(Dist.CLIENT)
    public void registerKeybinds() {
        this.worldWingKeyBind = new KeyBinding("key.world_wing", GLFW.GLFW_KEY_V, "key.categories.scol");

        ClientRegistry.registerKeyBinding(this.worldWingKeyBind);

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || !Minecraft.getInstance().isWindowActive()) {
            return;
        }

        if (this.worldWingKeyBind.consumeClick()) {
            Optional<SlotResult> result = CuriosApi.getCuriosHelper().findFirstCurio(Minecraft.getInstance().player, ScolItems.WORLD_WING);
            if (result.isPresent()) {
                Main.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketWorldWing(true));
                ItemStack stack = result.get().getStack();
                if (WorldWing.getFlySpeedInt(stack) == 4) {
                    Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("chat.send.wingspeed", 0), PacketWorldWing.uuidSend);
                } else {
                    Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("chat.send.wingspeed", WorldWing.getFlySpeedInt(stack)+1), PacketWorldWing.uuidSend);
                }
            }
        }





    }
}
