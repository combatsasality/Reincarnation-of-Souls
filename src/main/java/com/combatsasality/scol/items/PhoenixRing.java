package com.combatsasality.scol.items;

import com.combatsasality.scol.capabilities.ScolCapability;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolCapabilities;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import java.util.List;

public class PhoenixRing extends Item implements ICurioItem, ITab {
    public PhoenixRing() {
        super(new Properties().fireResistant().stacksTo(1));
    }

    public static int getFloatForChickRing(ItemStack item) {
        if (item.getHoverName().getString().equalsIgnoreCase("\u043a\u043e\u043b\u044c\u0446\u043e \u043f\u0442\u0435\u043d\u0446\u0430") || item.getHoverName().getString().equalsIgnoreCase("chick ring")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.scol.empty"));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.scol.phoenix_ring.0"));
            tooltip.add(Component.translatable("tooltip.scol.phoenix_ring.1"));
        } else {
            tooltip.add(Component.translatable("tooltip.scol.hold_shift"));
        }

        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    public static boolean godModeIsActive(ItemStack stack) {
        return stack.getOrCreateTag().getInt("scol.GodMode") != 0;
    }

    public static void godModeActived(ItemStack stack) {
        stack.getOrCreateTag().putInt("scol.GodMode", 3600);
    }

    public static void godModeConsume(ItemStack stack) {
        stack.getOrCreateTag().putInt("scol.GodMode", stack.getOrCreateTag().getInt("scol.GodMode") - 1);
    }

    @NotNull
    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        if (godModeIsActive(stack)) {
            return ICurio.DropRule.ALWAYS_KEEP;
        }
        return ICurio.DropRule.DEFAULT;
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return !godModeIsActive(stack);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity player = slotContext.entity();
        if (!player.level().isClientSide) {
            if (player.isOnFire()) player.clearFire();
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, false, false, false));
            LazyOptional<ScolCapability.IScolCapability> capability = player.getCapability(ScolCapabilities.SCOL_CAPABILITY);
            capability.ifPresent(capa -> {
                if (capa.getCoolDownPhoenixRing() != 0) {
                    capa.consumeCoolDownPhoenixRing(0);
                }
            });
            if (godModeIsActive(stack)) {
                godModeConsume(stack);
                player.setHealth(player.getMaxHealth());
            }
            if (player.getHealth() <= 0.0F && capability.map(capa -> capa.canUsePhoenixRing()).orElse(true)) {
                player.revive();
                player.setHealth(player.getMaxHealth());
                capability.ifPresent(capa -> capa.setCoolDownPhoenixRing(15600));
                godModeActived(stack);
            }
        }


        ICurioItem.super.curioTick(slotContext, stack);
    }
}
