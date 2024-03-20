package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.registries.ScolItems;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class InactivePhoenixRing extends Item implements ICurioItem {
    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("223c79e0-bb84-41a9-93e2-99adccbd93be");
    public InactivePhoenixRing() {
        super(new Properties().tab(Main.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> list, ITooltipFlag tooltip) {
        list.add(new TranslationTextComponent("tooltip.scol.empty"));
        if (Screen.hasShiftDown()) {
            list.add(new TranslationTextComponent("tooltip.scol.phoenix_ring_inactive.0"));
            list.add(new TranslationTextComponent("tooltip.scol.phoenix_ring_inactive.1"));
        } else {
            list.add(new TranslationTextComponent("tooltip.scol.hold_shift"));
        }
        super.appendHoverText(stack, p_77624_2_, list, tooltip);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        PlayerEntity player = (PlayerEntity) slotContext.getWearer();
        ModifiableAttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeModifier modifier = health.getModifier(HEALTH_MODIFIER_UUID);
        if (modifier == null) {
            health.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, Main.MODID+":remove_health_inactive_phoenix_ring", -1.0F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        float amount = player.getHealth() - player.getMaxHealth();
        if (amount > 0) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ModifiableAttributeInstance health = ((PlayerEntity) slotContext.getWearer()).getAttribute(Attributes.MAX_HEALTH);
        if (health.getModifier(HEALTH_MODIFIER_UUID) != null) {
            health.removeModifier(HEALTH_MODIFIER_UUID);
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canUnequip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).isCreative() || livingEntity instanceof PlayerEntity && CuriosApi.getCuriosHelper().findFirstCurio(livingEntity, ScolItems.PHOENIX_RING).isPresent();
    }

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(LivingEntity livingEntity, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }
}
