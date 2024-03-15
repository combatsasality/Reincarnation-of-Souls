package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.ScolCapabality;
import com.combatsasality.scol.packets.server.PacketGetCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PhoenixRing extends Item implements ICurioItem {
    public PhoenixRing() {
        super(new Properties().fireResistant().tab(Main.TAB).stacksTo(1));
    }

    public static int getFloatForChickRing(ItemStack item) {
        if (item.getHoverName().getString().equalsIgnoreCase("\u043a\u043e\u043b\u044c\u0446\u043e \u043f\u0442\u0435\u043d\u0446\u0430") || item.getHoverName().getString().equalsIgnoreCase("chick ring")) {
            return 1;
        } else {
            return 0;
        }
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag tooltip) {
        if (world != null) {
            Main.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketGetCapability(true));
            list.add(Minecraft.getInstance().player.getCapability(ScolCapabality.NeedVariables).map(capa -> capa.canUsePhoenixRing()).orElse(true) ? new TranslationTextComponent("tooltip.scol.active") : new TranslationTextComponent("tooltip.scol.inactive"));
        }
        list.add(new TranslationTextComponent("tooltip.scol.empty"));
        if (Screen.hasShiftDown()) {
            list.add(new TranslationTextComponent("tooltip.scol.phoenix_ring.0"));
            list.add(new TranslationTextComponent("tooltip.scol.phoenix_ring.1"));
        } else {
            list.add(new TranslationTextComponent("tooltip.scol.hold_shift"));
        }
        super.appendHoverText(stack, world, list, tooltip);
    }
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!livingEntity.level.isClientSide) {
            if (livingEntity.isOnFire()) livingEntity.clearFire();
            livingEntity.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 100, 0, false, false, false));
            LazyOptional<ScolCapabality.DataCapability> capability = livingEntity.getCapability(ScolCapabality.NeedVariables);
            if (capability.map(capa -> capa.getCoolDownPhoenixRing()).orElse(0) != 0) {
                capability.ifPresent(capa -> capa.consumeCoolDownPhoenixRing(1));
            }
            if (godModeIsActive(stack)) {
                godModeConsume(stack);
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }
            if (livingEntity.getHealth() <= 0.0F && capability.map(capa -> capa.canUsePhoenixRing()).orElse(true)) {
                livingEntity.revive();
                livingEntity.setHealth(livingEntity.getMaxHealth());
                capability.ifPresent(capa -> capa.setCoolDownPhoenixRing(15600));
                godModeActived(stack);
            }

            ICurioItem.super.curioTick(identifier, index, livingEntity, stack);
        }
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

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(LivingEntity livingEntity, ItemStack stack) {
        if (godModeIsActive(stack)) {
            return ICurio.DropRule.ALWAYS_KEEP;
        }
        return ICurio.DropRule.DEFAULT;
    }

    @Override
    public boolean canUnequip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return !godModeIsActive(stack);
    }


}
