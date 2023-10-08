package scol.handlers;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import scol.Main;
import scol.items.PhoenixRing;
import scol.scolCapability;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler {

    @SubscribeEvent
    public void DeathEvent(LivingDeathEvent event) {
        if (!event.getEntity().level.isClientSide) {
            if (event.getSource().getEntity() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getSource().getEntity();
                ItemStack stack = player.getMainHandItem();
                CompoundNBT nbt = stack.getOrCreateTag();
                if (stack.getItem().equals(Main.frostMourne) && nbt.getInt("scol.souls") < 100) {
                    nbt.putInt("scol.souls", nbt.getInt("scol.souls") + 1);
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
            ItemModelsProperties.register(Main.phoenixRing.getItem(), new ResourceLocation("scol:chick"), (itemStack, clientWorld, livingEntity) -> PhoenixRing.GetFloatFor(itemStack));
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
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                        player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 30, 0));
                        player.inventory.getItem(dragonSoul).shrink(1);
                        player.inventory.getItem(witherSoul).shrink(1);
                        CuriosApi.getCuriosHelper().getCuriosHandler(player).map(capa -> capa.getCurios().get("ring")).get().getStacks().setStackInSlot(slotResult.getSlotContext().getIndex(), new ItemStack(Main.phoenixRing));
                    }
                }
            });
        }
    }
}
