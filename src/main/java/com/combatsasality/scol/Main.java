package com.combatsasality.scol;

import com.combatsasality.scol.capabilities.ScolCapability;
import com.combatsasality.scol.entity.IchigoVizard;
import com.combatsasality.scol.handlers.EventHandler;
import com.combatsasality.scol.handlers.KeyBindHandler;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.packets.client.PacketSetCapability;
import com.combatsasality.scol.packets.server.PacketGetCapability;
import com.combatsasality.scol.packets.server.PacketWorldWing;
import com.combatsasality.scol.proxy.ClientProxy;
import com.combatsasality.scol.proxy.CommonProxy;
import com.combatsasality.scol.registries.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

@Mod(Main.MODID)
public class Main {
    public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    private static final String PTC_VERSION = "1";
    public static SimpleChannel packetInstance;
    public static final String MODID = "scol";
    public static final String VERSION = "1.0";
    public static final String VERSION_MINECRAFT = "1.16.5";
    public static KeyBindHandler keyBindHandler;


    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistries);
        FMLJavaModLoadingContext.get().getModEventBus().register(PROXY);
        keyBindHandler = new KeyBindHandler();

        MinecraftForge.EVENT_BUS.register(PROXY);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(keyBindHandler);
        new ScolTabs();
        new ScolEnchantments();
        new ScolSounds();
        new ScolItems();
        new ScolAttributes();
        new ScolLootModifiers();
        new ScolEntities();
    }

    private void setup(final FMLCommonSetupEvent event) {
        packetInstance = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main")).networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals).serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();
        packetInstance.registerMessage(0, PacketSetCapability.class, PacketSetCapability::encode, PacketSetCapability::decode, PacketSetCapability::handle);
        packetInstance.registerMessage(1, PacketGetCapability.class, PacketGetCapability::encode, PacketGetCapability::decode, PacketGetCapability::handle);
        packetInstance.registerMessage(2, PacketWorldWing.class, PacketWorldWing::encode, PacketWorldWing::decode, PacketWorldWing::handle);
    }

    private void clientRegistries(FMLClientSetupEvent event) {
        ScolItems.PHOENIX_RING.registerChick();
        PROXY.initEntityRendering();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterKeyBinds(RegisterKeyMappingsEvent event) {
        keyBindHandler.worldWingKeyBind = new KeyMapping("key.world_wing", GLFW.GLFW_KEY_V, "key.categories.scol");

        event.register(keyBindHandler.worldWingKeyBind);
    }

    @SubscribeEvent
    public void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof ITab member) {
                if (event.getTab() != member.getCreativeTab()) return;

                member.getCreativeTabStacks().forEach(event::accept);
            }
            if (item.equals(ScolItems.MUSIC_DISC_METAL_3) || item.equals(ScolItems.MUSIC_DISC_SILENT_RELAPSE)) {
                if (event.getTab() != ScolTabs.MAIN) return;
                event.accept(item);
            };
        });
    }

    @SubscribeEvent
    public void addAttribute(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ScolAttributes.MAGICAL_DAMAGE);

    }
    @SubscribeEvent
    public void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ScolEntities.ICHIGO_VAZARD, IchigoVizard.createAttributes().build());
    }

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(ScolCapability.IScolCapability.class);
    }


}
