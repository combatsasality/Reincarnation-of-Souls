package scol;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scol.blocks.SoulBlock;
import scol.enchantment.AttackSpeedEnchant;
import scol.enchantment.SoulCatcherEnchant;
import scol.enchantment.VampiricEnchant;
import scol.entity.CustomItemEntity;
import scol.entity.IchigoVizard;
import scol.entity.Onryo;
import scol.entity.projectile.PowerWaveEntity;
import scol.handlers.EventHandler;
import scol.handlers.HelpHandler;
import scol.handlers.KeyBindHandler;
import scol.handlers.lootModifiers.EntityAdditionModifier;
import scol.handlers.lootModifiers.StructureAdditionModifier;
import scol.items.*;
import scol.items.generic.ISoulMaterial;
import scol.items.generic.ItemBase;
import scol.packets.client.PacketCapa;
import scol.packets.client.PacketSetModelType;
import scol.packets.server.PacketGetCapability;
import scol.packets.server.PacketWorldWing;
import scol.proxy.ClientProxy;
import scol.proxy.CommonProxy;
import scol.world.structures.Graveyard;
import top.theillusivec4.curios.api.SlotTypeMessage;

import javax.annotation.Nonnull;

@Mod(Main.modid)
public class Main {
    public static final CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public static final String modid = "scol";
    public static final Logger logger = LogManager.getLogger();
    private static final String PTC_VERSION = "1";
    public static SimpleChannel packetInstance;
    public static Attribute MAGICAL_DAMAGE;
    public static KeyBindHandler keyBinds;
    public static TestItem testItem;
    public static FrostMourne frostMourne;
    public static ItemBase bladeFrost;
    public static ItemBase handleFrost;
    public static PhoenixRing phoenixRing;
    public static Zangetsu zangetsu;
    public static ForgeSpawnEggItem onryoSpawnEgg;
    public static final ItemGroup TAB = new ItemGroup("scolTab") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Main.frostMourne);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fillItemList(NonNullList<ItemStack> itemStack) {
            for (Item item : ForgeRegistries.ITEMS) {
                if (Minecraft.getInstance().player != null && item.equals(Main.zangetsu)) {
                    ItemStack stack = new ItemStack(Main.zangetsu);
                    stack.getOrCreateTag().putString("scol.Owner", Minecraft.getInstance().player.getGameProfile().getName());
                    itemStack.add(stack);
                    continue;
                }
                item.fillItemCategory(this, itemStack);
                if (item.equals(Main.frostMourne)) {
                    ItemStack frostMourneWithSouls = new ItemStack(Main.frostMourne);
                    frostMourneWithSouls.getOrCreateTag().putInt("scol.Souls", 100);
                    itemStack.add(frostMourneWithSouls);
                }
            }
        }
    };

    public static AbstractGlassBlock soulGlass;
    public static SoulBlock soulBlock;
    public static SoulBlock soulAggressiveBlock;
    public static InactivePhoenixRing inactivePhoenixRing;
    public static ItemBase dragonSoul;
    public static ItemBase witherSoul;
    public static ItemBase firstPartMask;
    public static ItemBase secondPartMask;
    public static ItemBase thirdPartMask;
    public static ItemBase fourthPartMask;
    public static SummonMask summonMask;
    public static WorldWing worldWing;
    public static RingMidas ringMidas;
    public static Soul soul;
    public static Soul aggressiveSoul;
    public static Soul friendlySoul;
    public static BlockItem soulGlassItem;
    public static BlockItem soulBlockItem;
    public static BlockItem soulAggressiveBlockItem;
    public static SoundEvent sonidoSound;
    public static SoundEvent metalMusic;
    public static SoundEvent silentRelapseMusic;
    public static MusicDiscItem metalDisc;
    public static MusicDiscItem silentRelapseDisc;

    public static VampiricEnchant vampiricEnchant;
    public static AttackSpeedEnchant attackSpeedEnchant;
    public static SoulCatcherEnchant soulCatcherEnchant;
    public static Potion potionHeroOfVillage;
    public static Potion potionStrongHeroOfVillage;
    public static Potion potionLongHeroOfVillage;

    public static Structure<NoFeatureConfig> graveyardCombatsasality;
    public static StructureFeature<?, ?> configuredGraveyardCombastsasality;

    public static Structure<NoFeatureConfig> graveyardDesert;
    public static StructureFeature<?, ?> configuredGraveyardDesert;

    public static Structure<NoFeatureConfig> graveyardForest;
    public static StructureFeature<?, ?> configuredGraveyardForest;

    public static Structure<NoFeatureConfig> graveyardMountains;
    public static StructureFeature<?, ?> configuredGraveyardMountains;

    public static Structure<NoFeatureConfig> graveyardTaiga;
    public static StructureFeature<?, ?> configuredGraveyardTaiga;

    public static Structure<NoFeatureConfig> graveyardNether;
    public  static StructureFeature<?, ?> configuredGraveyardNether;


    public Main() {
        // Attributes
        MAGICAL_DAMAGE = new RangedAttribute("attribute.name.scol.magical_damage", 0.0D, 0.0d, 100000000).setRegistryName("magical_damage").setSyncable(true);

        // Key binds
        keyBinds = new KeyBindHandler();

        // Sounds
        sonidoSound = new SoundEvent(new ResourceLocation("scol:sonido")).setRegistryName("sonido");
        metalMusic = new SoundEvent(new ResourceLocation("scol:music.metal_3")).setRegistryName("music.metal_3");
        silentRelapseMusic = new SoundEvent(new ResourceLocation("scol:music.silent_relapse")).setRegistryName("music.silent_relapse");

        // Music disc
        Item.Properties discProperties = new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).tab(Main.TAB);
        metalDisc = new MusicDiscItem(1, () -> metalMusic, discProperties);
        metalDisc.setRegistryName("music_disc_metal_3");
        silentRelapseDisc = new MusicDiscItem(1, () -> silentRelapseMusic, discProperties);
        silentRelapseDisc.setRegistryName("music_disc_silent_relapse");

        // Blocks
        // TODO: Replace default glass sound
        soulGlass = new GlassBlock(AbstractBlock.Properties.copy(Blocks.GLASS));
        soulGlass.setRegistryName("soul_glass");
        soulBlock = new SoulBlock("soul_block");
        soulAggressiveBlock = new SoulBlock("aggressive_soul_block");


        // Items
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
        ringMidas = new RingMidas();
        soul = new Soul("soul", 3100, ISoulMaterial.SoulType.NEGATIVE);
        aggressiveSoul = new Soul("aggressive_soul", 6200, ISoulMaterial.SoulType.AGGRESSIVE);
        friendlySoul = new Soul("friendly_soul", 1550, ISoulMaterial.SoulType.FRIENDLY);
        soulGlassItem = new BlockItem(soulGlass, new Item.Properties().rarity(Rarity.RARE).tab(Main.TAB));
        soulGlassItem.setRegistryName("soul_glass");
        soulBlockItem = new BlockItem(soulBlock, new Item.Properties().tab(Main.TAB));
        soulBlockItem.setRegistryName("soul_block");
        soulAggressiveBlockItem = new BlockItem(soulAggressiveBlock, new Item.Properties().tab(Main.TAB));
        soulAggressiveBlockItem.setRegistryName("aggressive_soul_block");


        // Enchantments
        vampiricEnchant = new VampiricEnchant();
        attackSpeedEnchant = new AttackSpeedEnchant();
        soulCatcherEnchant = new SoulCatcherEnchant();

        // Potions
        potionHeroOfVillage = new Potion("hero_of_village", new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 3600));
        potionHeroOfVillage.setRegistryName("potion_hero_of_village");
        potionLongHeroOfVillage = new Potion("hero_of_village", new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 9600));
        potionLongHeroOfVillage.setRegistryName("potion_long_hero_of_village");
        potionStrongHeroOfVillage = new Potion("hero_of_village", new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 1800, 1));
        potionStrongHeroOfVillage.setRegistryName("potion_strong_hero_of_village");

        // Potion brewing
        PotionBrewing.addMix(Potions.WATER, Items.EMERALD_BLOCK, potionHeroOfVillage);
        PotionBrewing.addMix(potionHeroOfVillage, Items.BROWN_MUSHROOM, potionStrongHeroOfVillage);
        PotionBrewing.addMix(potionHeroOfVillage, Items.GLISTERING_MELON_SLICE, potionLongHeroOfVillage);

        // Spawn Egg
        onryoSpawnEgg = new ForgeSpawnEggItem(() -> Onryo.TYPE, 0xFFFFFF, 0x40E0D0, new Item.Properties().tab(ItemGroup.TAB_MISC));
        onryoSpawnEgg.setRegistryName("onryo_spawn_egg");

        //Structure
        graveyardCombatsasality = new Graveyard(NoFeatureConfig.CODEC, "graveyard_combatsasality");
        configuredGraveyardCombastsasality = graveyardCombatsasality.configured(IFeatureConfig.NONE);
        graveyardDesert = new Graveyard(NoFeatureConfig.CODEC, "graveyard_desert");
        configuredGraveyardDesert = graveyardDesert.configured(IFeatureConfig.NONE);
        graveyardForest = new Graveyard(NoFeatureConfig.CODEC, "graveyard_forest");
        configuredGraveyardForest = graveyardForest.configured(IFeatureConfig.NONE);
        graveyardMountains = new Graveyard(NoFeatureConfig.CODEC, "graveyard_mountains");
        configuredGraveyardMountains = graveyardMountains.configured(IFeatureConfig.NONE);
        graveyardTaiga = new Graveyard(NoFeatureConfig.CODEC, "graveyard_taiga");
        configuredGraveyardTaiga = graveyardTaiga.configured(IFeatureConfig.NONE);
        graveyardNether = new Graveyard(NoFeatureConfig.CODEC, "graveyard_nether", true);
        configuredGraveyardNether = graveyardNether.configured(IFeatureConfig.NONE);

        // Regs
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
        packetInstance.registerMessage(2, PacketGetCapability.class, PacketGetCapability::encode, PacketGetCapability::decode, PacketGetCapability::handle);
        packetInstance.registerMessage(3, PacketSetModelType.class, PacketSetModelType::encode, PacketSetModelType::decode, PacketSetModelType::handle);
        proxy.setupClient(event);
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

    private void processIMC(final InterModProcessEvent event) {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void blockRegistry(final RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                    soulGlass,
                    soulBlock,
                    soulAggressiveBlock
            );
        }
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
                    ringMidas,
                    metalDisc,
                    onryoSpawnEgg,
                    silentRelapseDisc,
                    soul,
                    aggressiveSoul,
                    friendlySoul,
                    soulGlassItem,
                    soulBlockItem,
                    soulAggressiveBlockItem
            );
        }

        @SubscribeEvent
        public static void potionRegistry(final RegistryEvent.Register<Potion> event) {
            event.getRegistry().registerAll(
                    potionHeroOfVillage,
                    potionStrongHeroOfVillage,
                    potionLongHeroOfVillage
            );
        }

        @SubscribeEvent
        public static void registerEnchants(final RegistryEvent.Register<Enchantment> event) {
            event.getRegistry().registerAll(
                    vampiricEnchant,
                    attackSpeedEnchant,
                    soulCatcherEnchant
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
                            (new ResourceLocation("scol:part_mask_in_end_city")),
                    new EntityAdditionModifier.Serializer(0.99F, 0F).setRegistryName
                            (new ResourceLocation("scol:sound_disk_from_pig"))
            );
        }

        @SubscribeEvent
        public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
            event.getRegistry().registerAll(
                    sonidoSound,
                    metalMusic,
                    silentRelapseMusic
            );
        }

        @SubscribeEvent
        public static void onEntitiesRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            event.getRegistry().registerAll(
                    EntityType.Builder.<CustomItemEntity>of(CustomItemEntity::new, EntityClassification.MISC).sized(0.25F, 2.0F)
                            .setTrackingRange(64).setCustomClientFactory((spawnEntity, world) -> new CustomItemEntity(CustomItemEntity.TYPE, world))
                            .setUpdateInterval(2).setShouldReceiveVelocityUpdates(true).build(modid + ":custom_item_entity_ent").
                            setRegistryName(new ResourceLocation(modid, "custom_item_entity_ent")),
                    EntityType.Builder.<IchigoVizard>of(IchigoVizard::new, EntityClassification.MISC).sized(0.75f, 1.85f)
                            .setTrackingRange(64).setCustomClientFactory((spawnEntity, world) -> new IchigoVizard(IchigoVizard.TYPE, world))
                            .build(Main.modid + ":ichigo_vizard").setRegistryName("ichigo_vizard"),
                    EntityType.Builder.<PowerWaveEntity>of(PowerWaveEntity::new, EntityClassification.MISC).sized(4.0F, 0.1F)
                            .noSave()
                            .setCustomClientFactory((spawnEntity, world) -> new PowerWaveEntity(PowerWaveEntity.TYPE, world))
                            .setTrackingRange(64).build(Main.modid + ":power_wave").setRegistryName("power_wave"),
                    EntityType.Builder.<Onryo>of(Onryo::new, EntityClassification.MISC
                            ).sized(0.6f, 1.95f)
                            .setTrackingRange(64).setCustomClientFactory((spawnEntity, world) -> new Onryo(Onryo.TYPE, world))
                            .build(Main.modid + ":onryo").setRegistryName("onryo")
            );


        }

        @SubscribeEvent
        public static void addEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(IchigoVizard.TYPE, IchigoVizard.setCustomAttributes().build());
            event.put(Onryo.TYPE, Onryo.setCustomAttributes().build());
        }

        @SubscribeEvent
        public static void registerStructure(final RegistryEvent.Register<Structure<?>> event) {
//            HelpHandler.registerStructure(event.getRegistry(), graveyard, "graveyard");
//            HelpHandler.setupMapSpacingAndLand(
//                    graveyard,
//                    new StructureSeparationSettings(
//                            10,
//                            5,
//                            123456890
//                    ),
//                    true
//            );
//
//            Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
//            Registry.register(registry, new ResourceLocation(Main.modid, "configured_graveyard"), configuredGraveyard);
//            FlatGenerationSettings.STRUCTURE_FEATURES.put(graveyard, configuredGraveyard);
            IForgeRegistry<Structure<?>> registry = event.getRegistry();
            HelpHandler.registerStructure(registry, "graveyard_combatsasality", graveyardCombatsasality, configuredGraveyardCombastsasality, 150, 100);
            HelpHandler.registerStructure(registry, "graveyard_desert", graveyardDesert, configuredGraveyardDesert, 25, 20);
            HelpHandler.registerStructure(registry, "graveyard_forest", graveyardForest, configuredGraveyardForest, 25, 20);
            HelpHandler.registerStructure(registry, "graveyard_mountains", graveyardMountains, configuredGraveyardMountains, 25, 20);
            HelpHandler.registerStructure(registry, "graveyard_taiga", graveyardTaiga, configuredGraveyardTaiga, 25, 20);
            HelpHandler.registerStructure(registry, "graveyard_nether", graveyardNether, configuredGraveyardNether, 13, 10);
        }

    }
}
