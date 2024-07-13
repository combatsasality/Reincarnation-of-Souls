package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolItems;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class InactivePhoenixRing extends Item implements ICurioItem, ITab {
    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("223c79e0-bb84-41a9-93e2-99adccbd93be");

    public InactivePhoenixRing() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.scol.empty"));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.scol.phoenix_ring_inactive.0"));
            tooltip.add(Component.translatable("tooltip.scol.phoenix_ring_inactive.1"));
        } else {
            tooltip.add(Component.translatable("tooltip.scol.hold_shift"));
        }
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        AttributeInstance health = slotContext.entity().getAttribute(Attributes.MAX_HEALTH);
        if (health.getModifier(HEALTH_MODIFIER_UUID) != null) {
            health.removeModifier(HEALTH_MODIFIER_UUID);
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return false;
        return player.isCreative() || CuriosApi.getCuriosInventory(player).map(inventory -> inventory.findFirstCurio(ScolItems.PHOENIX_RING).isPresent()).orElse(false);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Player player = (Player) slotContext.entity();
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeModifier modifier = health.getModifier(HEALTH_MODIFIER_UUID);
        if (modifier == null) {
            health.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, Main.MODID+":remove_health_inactive_phoenix_ring", -1.0F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        float amount = player.getHealth() - player.getMaxHealth();
        if (amount > 0) {
            player.setHealth(player.getMaxHealth());
        }
        ICurioItem.super.onEquip(slotContext, prevStack, stack);
    }

    @NotNull
    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }
}
