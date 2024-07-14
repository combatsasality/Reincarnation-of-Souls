package com.combatsasality.scol.handlers;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.capabilities.ScolCapability;
import com.combatsasality.scol.items.PhoenixRing;
import com.combatsasality.scol.registries.ScolCapabilities;
import com.combatsasality.scol.registries.ScolEnchantments;
import com.combatsasality.scol.registries.ScolItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler {

    @SubscribeEvent
    public void frostmourneKill(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            ItemStack stack = player.getMainHandItem();
            CompoundTag tag = stack.getOrCreateTag();
            if (stack.getItem().equals(ScolItems.FROSTMOURNE) && tag.getInt("scol.Souls") < 100) {
                tag.putInt("scol.Souls", tag.getInt("scol.Souls") + 1);
            }
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void toolTipForMagicalDamage(ItemTooltipEvent event) { //It's a bullshit code, so what? If you know how to do it in a non-bullshit way, write me :D
        int index = 0;
        if (!event.getItemStack().getItem().equals(ScolItems.FROSTMOURNE)) return;
        for (Component text : event.getToolTip()) {
            index++;
            if (!(text instanceof MutableComponent mutableComponent)) continue;
            if (!(mutableComponent.getContents() instanceof TranslatableContents translatableContents)) continue;
            if (!translatableContents.getKey().equalsIgnoreCase("attribute.modifier.plus.0")) continue;
            if (!(translatableContents.getArgs()[1] instanceof MutableComponent magicalComponent)) continue;
            if (!(magicalComponent.getContents() instanceof TranslatableContents magicalTranslatable)) continue;
            if (!magicalTranslatable.getKey().equalsIgnoreCase("attribute.name.scol.magical_damage")) continue;


            Object[] objects = new Object[]{translatableContents.getArgs()[0], translatableContents.getArgs()[1]};
            MutableComponent newComponent = Component.translatable("attribute.modifier.equals.0", objects);
            newComponent.withStyle(ChatFormatting.DARK_PURPLE);
            event.getToolTip().set(index-1, newComponent);

        }

    }

    @SubscribeEvent
    public void dropSoul(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            ItemStack stack = player.getMainHandItem();
            if (event.getEntity() instanceof EnderDragon) {
                if (!player.addItem(new ItemStack(ScolItems.DRAGON_SOUL))) player.drop(new ItemStack(ScolItems.DRAGON_SOUL), true);
            } else if (event.getEntity() instanceof WitherBoss && Math.random() > 0.75-stack.getEnchantmentLevel(Enchantments.MOB_LOOTING)*0.1) {
                if (!player.addItem(new ItemStack(ScolItems.WITHER_SOUL))) player.drop(new ItemStack(ScolItems.WITHER_SOUL), true);
            }
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer)) {
            event.addCapability(new ResourceLocation(Main.MODID, "capability"), ScolCapability.createProvider());
        }
    }

    @SubscribeEvent
    public void cloneCapabilityPlayer(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player player = event.getEntity();
        Player oldPlayer = event.getOriginal();

        oldPlayer.reviveCaps();

        player.getCapability(ScolCapabilities.SCOL_CAPABILITY).orElse(null).readTag(
                oldPlayer.getCapability(ScolCapabilities.SCOL_CAPABILITY).orElse(null).writeTag());

        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKillInactivePhoenix(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && event.getSource().typeHolder().is(DamageTypes.LAVA)) {
            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                inventory.findFirstCurio(ScolItems.INACTIVE_PHOENIX_RING).ifPresent(stack -> {
                    NonNullList<ItemStack> streamList = player.getInventory().items;
                    Optional<ItemStack> dragonStream = streamList.stream().filter(stack1 -> stack1.getItem().equals(ScolItems.DRAGON_SOUL)).findFirst();
                    Optional<ItemStack> witherStream = streamList.stream().filter(stack1 -> stack1.getItem().equals(ScolItems.WITHER_SOUL)).findFirst();
                    if (dragonStream.isPresent() && witherStream.isPresent()) {
                        inventory.setEquippedCurio("ring", stack.slotContext().index(), new ItemStack(ScolItems.PHOENIX_RING));
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 30, 0));
                        witherStream.get().shrink(1);
                        dragonStream.get().shrink(1);
                    }
                });
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKillPhoenixRing(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                inventory.findFirstCurio(ScolItems.PHOENIX_RING).ifPresent(stack -> {
                    LazyOptional<ScolCapability.IScolCapability> capability = player.getCapability(ScolCapabilities.SCOL_CAPABILITY);
                    if (capability.map(ScolCapability.IScolCapability::canUsePhoenixRing).orElse(true)) {
                        capability.ifPresent(capa -> capa.setCoolDownPhoenixRing(15600));
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                        PhoenixRing.godModeActived(stack.stack());
                    } else if (PhoenixRing.godModeIsActive(stack.stack())) {
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth());
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public void worldWingBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
            inventory.findFirstCurio(ScolItems.WORLD_WING).ifPresent(stack -> {
                if (!player.onGround()) {
                    if (event.getOriginalSpeed() < event.getNewSpeed() * 5) event.setNewSpeed(event.getNewSpeed() * 5);
                }
            });
        });
    }

    @SubscribeEvent
    public void doVampiric(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity player && !player.level().isClientSide && player.isAlive()) {
            ItemStack stack = player.getMainHandItem();
            int enchantLevel = stack.getEnchantmentLevel(ScolEnchantments.VAMPIRIC_ENCHANT);
            if (enchantLevel != 0) {
                player.heal((float) (event.getAmount() * (enchantLevel * 0.1 * 2)));
            }
        }
    }

    @SubscribeEvent
    public void doSpeedEnchant(ItemAttributeModifierEvent event) {
        int enchantLevel = event.getItemStack().getEnchantmentLevel(ScolEnchantments.ATTACK_SPEED_INCREASE);
        if (enchantLevel != 0) {
            if (event.getSlotType().equals(EquipmentSlot.MAINHAND) && event.getItemStack().getItem() instanceof SwordItem) {
                double attackSpeed = event.getModifiers().get(Attributes.ATTACK_SPEED).stream().mapToDouble(AttributeModifier::getAmount).sum();
                event.removeModifier(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4D, AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed + (enchantLevel * 0.5), AttributeModifier.Operation.ADDITION));
            }
        }
    }


}
