package com.combatsasality.scol;

import com.combatsasality.scol.handlers.*;
import com.combatsasality.scol.packets.client.PacketCapa;
import com.combatsasality.scol.packets.client.PacketSetModelType;
import com.combatsasality.scol.packets.server.PacketGetCapability;
import com.combatsasality.scol.packets.server.PacketWorldWing;
import com.combatsasality.scol.proxy.ClientProxy;
import com.combatsasality.scol.proxy.CommonProxy;
import com.combatsasality.scol.registries.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(Main.MODID)
public class Main {
    public static final CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public static final String MODID = "scol";
    public static final String VERSION = "0.1";
    public static final String VERSION_MINECRAFT = "1.16.5";
    public static final Logger logger = LogManager.getLogger();
    private static final String PTC_VERSION = "1";
    public static SimpleChannel packetInstance;
    public static KeyBindHandler keyBinds;
    public static final ItemGroup TAB = new ItemGroup("scolTab") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ScolItems.FROSTMOURNE);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fillItemList(NonNullList<ItemStack> itemStack) {
            for (Item item : ForgeRegistries.ITEMS) {
                if (Minecraft.getInstance().player != null && item.equals(ScolItems.ZANGETSU)) {
                    ItemStack stack = new ItemStack(ScolItems.ZANGETSU);
                    stack.getOrCreateTag().putString("scol.Owner", Minecraft.getInstance().player.getGameProfile().getName());
                    itemStack.add(stack);
                    continue;
                }
                item.fillItemCategory(this, itemStack);
                if (item.equals(ScolItems.FROSTMOURNE)) {
                    ItemStack frostmourneWithSouls = new ItemStack(ScolItems.FROSTMOURNE);
                    frostmourneWithSouls.getOrCreateTag().putInt("scol.Souls", 100);
                    itemStack.add(frostmourneWithSouls);
                }
            }
        }
    };


    public Main() {
        // Key binds
        keyBinds = new KeyBindHandler();

        new ScolItems();
        new ScolSounds();
        new ScolBlocks();
        new ScolAttributes();
        new ScolEnchantments();
        new ScolPotions();
        new ScolEntities();
        new ScolLootModifiers();
        new ScolStructures();
        new ScolTiles();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.addListener(CheckVersionHandler::worldEventLoad);
        MinecraftForge.EVENT_BUS.register(keyBinds);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG, "soul_collector-common.toml");

        // Potion brewing
        PotionBrewing.addMix(Potions.WATER, Items.EMERALD_BLOCK, ScolPotions.HERO_OF_VILLAGE);
        PotionBrewing.addMix(ScolPotions.HERO_OF_VILLAGE, Items.BROWN_MUSHROOM, ScolPotions.STRONG_HERO_OF_VILLAGE);
        PotionBrewing.addMix(ScolPotions.HERO_OF_VILLAGE, Items.GLISTERING_MELON_SLICE, ScolPotions.LONG_HERO_OF_VILLAGE);


    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ScolCapabality.DataCapability.class, new ScolCapabality.DataCapability.Storage(), ScolCapabality.DataCapability::new);
        packetInstance = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main")).networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals).serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();
        packetInstance.registerMessage(0, PacketCapa.class, PacketCapa::encode, PacketCapa::decode, PacketCapa::handle);
        packetInstance.registerMessage(1, PacketWorldWing.class, PacketWorldWing::encode, PacketWorldWing::decode, PacketWorldWing::handle);
        packetInstance.registerMessage(2, PacketGetCapability.class, PacketGetCapability::encode, PacketGetCapability::decode, PacketGetCapability::handle);
        packetInstance.registerMessage(3, PacketSetModelType.class, PacketSetModelType::encode, PacketSetModelType::decode, PacketSetModelType::handle);
        proxy.setupClient(event);
        ScolStructures.getStructureMap().forEach((name, data) -> {
            HelpHandler.registerStructure(name, data.structure, data.configuredStructure, data.average, data.minAverage);
        });
    }
    private void onLoadComplete(final FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        keyBinds.registerKeybinds();
        proxy.initEntityRendering();
        proxy.initPropertyOverrideRegistry(event);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("scolwings").icon(new ResourceLocation("curios:slot/empty_wing_slot")).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").size(2).build());
    }

    private void processIMC(final InterModProcessEvent event) {
    }
}
