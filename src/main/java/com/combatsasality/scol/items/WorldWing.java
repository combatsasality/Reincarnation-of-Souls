package com.combatsasality.scol.items;

import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class WorldWing extends Item implements ICurioItem, ITab {
    public WorldWing() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.scol.world_wing.2", getFlySpeedInt(stack)).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.scol.currentKeybind", KeyMapping.createNameSupplier("key.world_wing").get().getString().toUpperCase()).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.scol.empty"));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.scol.world_wing.0"));
            tooltip.add(Component.translatable("tooltip.scol.world_wing.1"));
        } else {
            tooltip.add(Component.translatable("tooltip.scol.hold_shift"));
        }

        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }

    public static int getFlySpeedInt(ItemStack stack) {return stack.getOrCreateTag().getInt("scol.Speed");}

    public static void setFlySpeed(ItemStack stack,int speed) {
        stack.getOrCreateTag().putInt("scol.Speed", speed);
    }
    public static float getFlySpeed(ItemStack stack) {
        return stack.getOrCreateTag().getInt("scol.Speed")*0.1F+0.05F;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return (slotContext.entity() instanceof Player player) && player.isCreative();
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @NotNull
    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
        Abilities abilities = player.getAbilities();
        if (!abilities.mayfly) {
            abilities.mayfly = true;
            player.onUpdateAbilities();
        }
        if (abilities.getFlyingSpeed() != getFlySpeed(stack)) {
            abilities.setFlyingSpeed(getFlySpeed(stack));
            abilities.flying = true;
            player.onUpdateAbilities();
        }

        ICurioItem.super.curioTick(slotContext, stack);
    }
}
