package scol.handlers;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import scol.Main;
import scol.entity.CustomItemEntity;
import scol.entity.Onryo;
import scol.items.PhoenixRing;
import scol.items.Zangetsu;
import scol.scolCapability;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Iterator;
import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler {

    @SubscribeEvent
    public void DeathEvent(LivingDeathEvent event) {
        if (!event.getEntity().level.isClientSide) {
            if (event.getSource().getEntity() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getSource().getEntity();
                ItemStack stack = player.getMainHandItem();
                CompoundNBT nbt = stack.getOrCreateTag();
                if (stack.getItem().equals(Main.frostMourne) && nbt.getInt("scol.Souls") < 100) {
                    nbt.putInt("scol.Souls", nbt.getInt("scol.Souls") + 1);
                }   else if (event.getEntity() instanceof EnderDragonEntity) {
                    if (!player.addItem(new ItemStack(Main.dragonSoul))) player.drop(new ItemStack(Main.dragonSoul), true);
                } else if (event.getEntity() instanceof WitherEntity && Math.random() > 0.75- EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, stack)*0.1) {
                    if (!player.addItem(new ItemStack(Main.witherSoul))) player.drop(new ItemStack(Main.witherSoul), true);
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void ToolTipForMagicalDamage(ItemTooltipEvent event) { //Да быдло код, и чо? Если знаете как это сделать не побыдлянски, напишите мне :D
        int index = 0;
        for (ITextComponent text : event.getToolTip()) {
            if (text.getClass().equals(TranslationTextComponent.class)) {
                for (Object e : ((TranslationTextComponent) text).getArgs()) {
                    if (e.getClass().equals(TranslationTextComponent.class)) {
                        if (((TranslationTextComponent) e).getKey().equals("attribute.name.scol.magical_damage")) {
                            Object[] objects = new Object[]{((TranslationTextComponent) text).getArgs()[0], ((TranslationTextComponent) text).getArgs()[1]};
                            TextComponent textcomp = new StringTextComponent(" ");
                            TranslationTextComponent comp = new TranslationTextComponent("attribute.modifier.equals.0", objects);
                            textcomp.append(comp);
                            comp.setStyle(Style.EMPTY.withColor(TextFormatting.DARK_PURPLE));
                            event.getToolTip().set(index, textcomp);
                        }
                    }
                }
            }
            index++;
        }
    }
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void toolTipForShiftNBT(ItemTooltipEvent event) { // Delete when create gradlew build
        if (Screen.hasShiftDown()) {
            event.getToolTip().add(new StringTextComponent(event.getItemStack().getOrCreateTag().toString()));
        }
    }

    @SubscribeEvent
    public static void addAttribute(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, Main.MAGICAL_DAMAGE);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemModelsProperties.register(Main.phoenixRing.getItem(), new ResourceLocation("scol:chick"), (itemStack, clientWorld, livingEntity) -> PhoenixRing.getFloatForChickRing(itemStack));
            ItemModelsProperties.register(Main.zangetsu.getItem(), new ResourceLocation("scol:zangetsu_model"), (itemStack, clientWorld, livingEntity) -> Zangetsu.getZangetsuModel(itemStack));
        });
    }

    @SubscribeEvent
    public void giveCapabilityPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity && !(event.getObject() instanceof FakePlayer)) {
            event.addCapability(new ResourceLocation("scol:capability"), new scolCapability.DataCapability.Provider());
        }
    }

    @SubscribeEvent
    public void cloneCapabilityPlayer(PlayerEvent.Clone event) {
        CompoundNBT oldNBT = event.getOriginal().getCapability(scolCapability.NeedVariables).map(oldCap -> oldCap.getNBT()).orElse(new CompoundNBT());
        event.getPlayer().getCapability(scolCapability.NeedVariables).ifPresent(newCap -> newCap.setNBT(oldNBT));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKillPhoenixRing(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            CuriosApi.getCuriosHelper().findFirstCurio(player, Main.phoenixRing).ifPresent(slotResult -> {
                ItemStack phoenixRing = slotResult.getStack();
                LazyOptional<scolCapability.DataCapability> capability = player.getCapability(scolCapability.NeedVariables);
                if (capability.map(capa -> capa.canUsePhoenixRing()).orElse(true)) {
                    capability.ifPresent(capa -> capa.setCoolDownPhoenixRing(15600));
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    PhoenixRing.godModeActived(phoenixRing);
                } else if (PhoenixRing.godModeIsActive(phoenixRing)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKillInActivePhoenix(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            CuriosApi.getCuriosHelper().findFirstCurio(player, Main.inactivePhoenixRing).ifPresent(slotResult -> {
                if (event.getSource().equals(DamageSource.LAVA)) {
                    int dragonSoul = player.inventory.findSlotMatchingItem(new ItemStack(Main.dragonSoul));
                    int witherSoul = player.inventory.findSlotMatchingItem(new ItemStack(Main.witherSoul));
                    if (dragonSoul != -1 && witherSoul != -1) {
                        CuriosApi.getCuriosHelper().getCuriosHandler(player).map(capa -> capa.getCurios().get("ring")).get().getStacks().setStackInSlot(slotResult.getSlotContext().getIndex(), new ItemStack(Main.phoenixRing));
                        event.setCanceled(true);
                        player.setHealth(0.5F);
                        player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 30, 0));
                        player.inventory.getItem(dragonSoul).shrink(1);
                        player.inventory.getItem(witherSoul).shrink(1);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void AddVilagerTrade(VillagerTradesEvent event) {
        if (event.getType().equals(VillagerProfession.ARMORER)) {
            ItemStack emerald = new ItemStack(Items.EMERALD);
            emerald.setCount(64);
            ItemStack nether = new ItemStack(Items.NETHERITE_INGOT);
            nether.setCount(5);
            event.getTrades().get(5).add(new BasicTrade(emerald, nether, new ItemStack(Main.firstPartMask), 2, 0, 0));
        } else if (event.getType().equals(VillagerProfession.CLERIC)) {
            ItemStack flesh = new ItemStack(Items.ROTTEN_FLESH);
            flesh.setCount(64);
            ItemStack bone = new ItemStack(Items.BONE);
            bone.setCount(64);
            event.getTrades().get(5).add(new BasicTrade(flesh, bone, new ItemStack(Main.thirdPartMask), 2, 0, 0));

        }
    }

    @SubscribeEvent
    public void EntityDroppingZang(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide) return;
        if (event.getEntity() instanceof ItemEntity && ((ItemEntity) event.getEntity()).getItem().getItem().equals(Main.zangetsu)) {
            ItemEntity ent = (ItemEntity) event.getEntity();
            if (Zangetsu.getOwner(ent.getItem()).isEmpty()) {
                event.setCanceled(true);
                return;
            }
            event.setCanceled(true);
            CustomItemEntity newEntity = new CustomItemEntity(ent.level, ent.getX(), ent.getY(), ent.getZ(), ent.getItem());
            newEntity.setPickupDelay(40);
            newEntity.setDeltaMovement(ent.getDeltaMovement());
            newEntity.setNoGravity(false);
            newEntity.setInvulnerable(false);
            event.getWorld().addFreshEntity(newEntity);
        }
    }
    @SubscribeEvent
    public void DropZangetsu(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            boolean check = false;
            Iterator<ItemEntity> iterator = event.getDrops().iterator();
            while (iterator.hasNext()) {
                ItemEntity entity = iterator.next();
                if (entity.getItem().getItem().equals(Main.zangetsu)) {
                    if (check || Zangetsu.getOwner(entity.getItem()).isEmpty()) {
                        entity.remove();
                    } else {
                        iterator.remove();
                        ItemStack stack = entity.getItem();
                        Zangetsu.setDeathModel(stack, true);
                        if (!stack.getHoverName().getString().equalsIgnoreCase("combatsasality")) {
                            Zangetsu.setDisableGravity(stack, true);
                        }
                        CustomItemEntity newEntity = new CustomItemEntity(player.level, player.getX(),player.getY(),player.getZ(), entity.getItem());
                        entity.remove();
                        player.level.addFreshEntity(newEntity);
                        check = true;
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void ringMidasDrop(LivingDropsEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getSource().getEntity();
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, Main.ringMidas).isPresent()) {
                float random = EnchantmentHelper.getMobLooting(player) * 0.01F + player.getRandom().nextFloat();
                if (random > 0.70) {
                    ItemEntity item = new ItemEntity(event.getEntity().level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
                    if (random > 0.97) {
                        ItemStack stack = new ItemStack(Items.NETHERITE_SCRAP);
                        stack.setCount(1 + EnchantmentHelper.getMobLooting(player));
                        item.setItem(stack);
                    } else if (random > 0.90) {
                        ItemStack stack = new ItemStack(Items.DIAMOND);
                        stack.setCount(1 + EnchantmentHelper.getMobLooting(player));
                        item.setItem(stack);
                    } else if (random > 0.80) {
                        ItemStack stack = new ItemStack(Items.GOLD_INGOT);
                        stack.setCount(1 + EnchantmentHelper.getMobLooting(player));
                        item.setItem(stack);
                    } else {
                        ItemStack stack = new ItemStack(Items.IRON_INGOT);
                        stack.setCount(1 + EnchantmentHelper.getMobLooting(player));
                        item.setItem(stack);
                    }
                    event.getDrops().clear();
                    event.getDrops().add(item);
                }
            }
        }
    }

    @SubscribeEvent
    public void doVampiric(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity && !event.getEntity().level.isClientSide() && event.getSource().getEntity().isAlive()) {
            int enchantLevel = EnchantmentHelper.getEnchantmentLevel(Main.vampiricEnchant, (LivingEntity) event.getSource().getEntity());
            LivingEntity entity = (LivingEntity) event.getSource().getEntity();
            if (enchantLevel != 0) {
                entity.heal((float) (event.getAmount() * (enchantLevel * 0.1 * 2)));
            }
        }
    }
    @SubscribeEvent
    public void doSpeedEnchant(ItemAttributeModifierEvent event) {
        int levelEnchant = EnchantmentHelper.getItemEnchantmentLevel(Main.attackSpeedEnchant, event.getItemStack());
        if (levelEnchant != 0) {
            if (event.getSlotType().equals(EquipmentSlotType.MAINHAND) && event.getItemStack().getItem() instanceof SwordItem) {
                double attack_speed = event.getModifiers().get(Attributes.ATTACK_SPEED).stream().mapToDouble(AttributeModifier::getAmount).sum();
                double attack_damage = event.getModifiers().get(Attributes.ATTACK_DAMAGE).stream().mapToDouble(AttributeModifier::getAmount).sum();
                event.removeModifier(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
                event.removeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attack_damage, AttributeModifier.Operation.ADDITION));
                if (attack_damage != 0) {
                    event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attack_damage, AttributeModifier.Operation.ADDITION));
                }
                event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", attack_speed+(levelEnchant*0.5), AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @SubscribeEvent
    public void summonOnryo(LivingDeathEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (entity.getRandom().nextDouble() > 0.99) {
                entity.level.addFreshEntity(new Onryo(entity.level, entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot));
            }
        }
    }

    @SubscribeEvent
    public void worldWingBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        if (CuriosApi.getCuriosHelper().findFirstCurio(player, predicate -> predicate.getItem().equals(Main.worldWing)).isPresent()) {
            if (!player.isOnGround()) {
                if (event.getOriginalSpeed() < event.getNewSpeed() * 5) event.setNewSpeed(event.getNewSpeed() * 5F);
            }
        }
    }
}
