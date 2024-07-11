package com.combatsasality.scol.items;

import com.combatsasality.scol.handlers.ItemTier;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolAttributes;
import com.combatsasality.scol.registries.ScolTabs;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Frostmourne extends SwordItem implements ITab {
    public Frostmourne() {
        super(ItemTier.FOR_ALL, 0, 0, new Properties().fireResistant().rarity(Rarity.EPIC));
    }
    public static UUID MAGICAL_DAMAGE_UUID = UUID.fromString("0ba7ecc3-67c9-42d1-8cbf-09eda475b958");

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.item.frostmourne.0", stack.getOrCreateTag().getInt("scol.Souls")).withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity || entity instanceof PartEntity<?>) {
            if (entity.hurt(entity.damageSources().indirectMagic(entity, player), (float) (player.getAttribute(ScolAttributes.MAGICAL_DAMAGE).getValue() * player.getAttackStrengthScale(1.0f)))) {
                player.doEnchantDamageEffects((LivingEntity) entity, player);
            }
        }

        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();

        if (slot == EquipmentSlot.MAINHAND) {
            map.put(ScolAttributes.MAGICAL_DAMAGE, new AttributeModifier(MAGICAL_DAMAGE_UUID, "Weapon modifier", stack.getOrCreateTag().getInt("scol.Souls")+10, AttributeModifier.Operation.ADDITION));
            map.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.2F, AttributeModifier.Operation.ADDITION));
        }

        return map.build();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !super.isEnchantable(stack);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }
}
