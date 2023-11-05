package scol;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scol.entity.CustomItemEntity;
import scol.entity.IchigoVazard;
import scol.handlers.EventHandler;
import scol.handlers.KeyBindHandler;
import scol.handlers.lootModifiers.EntityAdditionModifier;
import scol.handlers.lootModifiers.StructureAdditionModifier;
import scol.items.*;
import scol.items.generic.ItemBase;
import scol.packets.client.PacketCapa;
import scol.packets.server.PacketWorldWing;
import scol.proxy.ClientProxy;
import scol.proxy.CommonProxy;
import top.theillusivec4.curios.api.SlotTypeMessage;

import javax.annotation.Nonnull;

@Mod(Main.modid)
public class Main {
    public static final CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final String modid = "scol";
    public static final Logger logger = LogManager.getLogger();
    public static SimpleChannel packetInstance;
    private static final String PTC_VERSION = "1";

    public static Attribute MAGICAL_DAMAGE;
    public static KeyBindHandler keyBinds;
    public static TestItem testItem;
    public static FrostMourne frostMourne;
    public static ThunderBlade thunderBlade;
    public static ItemBase bladeFrost;
    public static ItemBase handleFrost;
    public static PhoenixRing phoenixRing;
    public static Zangetsu zangetsu;
    public static InactivePhoenixRing inactivePhoenixRing;
    public static ItemBase dragonSoul;
    public static ItemBase witherSoul;

    public static ItemBase firstPartMask;
    public static ItemBase secondPartMask;
    public static ItemBase thirdPartMask;
    public static ItemBase fourthPartMask;
    public static SummonMask summonMask;
    public static WorldWing worldWing;

    public static SoundEvent sonidoSound;
    public static SoundEvent bossMusicForFight;
    public static SoundEvent bossMusicForDisc;



    public Main() {
        //Attributes
        MAGICAL_DAMAGE = new RangedAttribute("attribute.name.scol.magical_damage", 0.0D, 0.0d, 100000000).setRegistryName("magical_damage").setSyncable(true);

        //Keybinds
        keyBinds = new KeyBindHandler();

        //Sounds
        sonidoSound = new SoundEvent(new ResourceLocation("scol:sonido")).setRegistryName("sonido");
        bossMusicForFight = new SoundEvent(new ResourceLocation("scol:music.boss_fight")).setRegistryName("music.boss_fight");
        bossMusicForDisc = new SoundEvent(new ResourceLocation("scol:music.boss_music")).setRegistryName("music.boss_music");


        //Items
        testItem = new TestItem();
        frostMourne = new FrostMourne();
        bladeFrost = new ItemBase("frostmourne_handle", new Item.Properties().fireResistant());
        handleFrost = new ItemBase("frostmourne_blade", new Item.Properties().fireResistant());
        phoenixRing = new PhoenixRing();
        inactivePhoenixRing = new InactivePhoenixRing();
        dragonSoul = new ItemBase("dragon_soul", new Item.Properties().fireResistant().rarity(Rarity.EPIC));
        witherSoul = new ItemBase("wither_soul", new Item.Properties().fireResistant().rarity(Rarity.create("RED", TextFormatting.RED)));
        worldWing = new WorldWing();
        firstPartMask = new ItemBase("part_mask_first");
        secondPartMask = new ItemBase("part_mask_second");
        thirdPartMask = new ItemBase("part_mask_third");
        fourthPartMask = new ItemBase("part_mask_fourth");
        summonMask = new SummonMask();
        zangetsu = new Zangetsu();
        thunderBlade = new ThunderBlade();


        //Regs

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(keyBinds);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(scolCapability.DataCapability.class, new scolCapability.DataCapability.Storage(), scolCapability.DataCapability::new);

        packetInstance = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(modid, "main")).networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals).serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();
        packetInstance.registerMessage(0, PacketCapa.class, PacketCapa::encode, PacketCapa::decode, PacketCapa::handle);
        packetInstance.registerMessage(1, PacketWorldWing.class, PacketWorldWing::encode, PacketWorldWing::decode, PacketWorldWing::handle);
    }

    private void onLoadComplete(final FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        keyBinds.registerKeybinds();
        proxy.initEntityRendering();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("scolwings").icon(new ResourceLocation("curios:slot/empty_wing_slot")).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").size(2).build());
    }

    private void processIMC(final InterModProcessEvent event) {}

    public static final ItemGroup TAB = new ItemGroup("scolTab") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {return new ItemStack(Main.frostMourne);}

    };


    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents{
        @SubscribeEvent
        public static void itemRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    testItem,
                    frostMourne,
                    bladeFrost,
                    handleFrost,
                    phoenixRing,
                    inactivePhoenixRing,
                    dragonSoul,
                    witherSoul,
                    worldWing,
                    firstPartMask,
                    secondPartMask,
                    thirdPartMask,
                    fourthPartMask,
                    summonMask,
                    zangetsu,
                    thunderBlade

            );
        }
        @SubscribeEvent
        public static void registerCustomAttribute(final RegistryEvent.Register<Attribute> event) {
            event.getRegistry().register(MAGICAL_DAMAGE);
        }
        @SubscribeEvent
        public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().registerAll(
                    new StructureAdditionModifier.Serializer(0.65F).setRegistryName
                            (new ResourceLocation("scol:handle_in_mineshaft")),
                    new StructureAdditionModifier.Serializer(0.40F).setRegistryName
                            (new ResourceLocation("scol:handle_in_igloo")),
                    new StructureAdditionModifier.Serializer(0.65F).setRegistryName
                            (new ResourceLocation("scol:handle_in_pillager_outpost")),
                    new StructureAdditionModifier.Serializer(0.70F).setRegistryName
                            (new ResourceLocation("scol:inactive_ring_in_netherbridge")),
                    new EntityAdditionModifier.Serializer(0.98F, 0.01F).setRegistryName
                            (new ResourceLocation("scol:part_mask_second_from_wither")),
                    new StructureAdditionModifier.Serializer(0.75F).setRegistryName
                            (new ResourceLocation("scol:part_mask_in_end_city"))
            );
        }

        @SubscribeEvent
        public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
            event.getRegistry().registerAll(
                    sonidoSound,
                    bossMusicForFight,
                    bossMusicForDisc
            );
        }

        @SubscribeEvent
        public static void onEntitiesRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            event.getRegistry().register(EntityType.Builder.<CustomItemEntity>of(CustomItemEntity::new, EntityClassification.MISC).sized(0.25F, 2.0F).setTrackingRange(64).setCustomClientFactory((spawnEntity, world) -> new CustomItemEntity(CustomItemEntity.TYPE, world)).setUpdateInterval(2).setShouldReceiveVelocityUpdates(true).build(modid+":custom_item_entity_ent").setRegistryName(new ResourceLocation(modid, "custom_item_entity_ent")));
            event.getRegistry().register(EntityType.Builder.<IchigoVazard>of(IchigoVazard::new, EntityClassification.MISC).sized(0.75f, 1.85f).build(Main.modid+":ichigo_vazard").setRegistryName("ichigo_vazard"));
        }

        @SubscribeEvent
        public static void addEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(IchigoVazard.TYPE, IchigoVazard.setCustomAttrbiutes().build());
        }
    }
}
