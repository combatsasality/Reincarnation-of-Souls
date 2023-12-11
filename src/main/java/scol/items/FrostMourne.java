package scol.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import scol.Main;
import scol.handlers.ItemTier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class FrostMourne extends SwordItem {
    public FrostMourne() {
        super(ItemTier.FOR_ALL, 0, 0, new Properties().fireResistant().tab(Main.TAB).rarity(Rarity.EPIC));
        this.setRegistryName("frostmourne");
    }

    public static UUID MAGICAL_DAMAGE_UUID = UUID.fromString("0ba7ecc3-67c9-42d1-8cbf-09eda475b958");

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World p_77624_2_, List<ITextComponent> list, ITooltipFlag p_77624_4_) {
        list.add(1 ,new TranslationTextComponent("tooltip.item.frostmourne.0", stack.getOrCreateTag().getInt("scol.Souls")).withStyle(TextFormatting.DARK_RED));
        super.appendHoverText(stack, p_77624_2_, list, p_77624_4_);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity || entity instanceof EnderDragonPartEntity) {
            entity.hurt(new EntityDamageSource("frostmourne_hurt", player).setMagic(), (float) (player.getAttribute(Main.MAGICAL_DAMAGE).getValue() * player.getAttackStrengthScale(1.0f)));
            EnchantmentHelper.doPostDamageEffects(player, entity);
            return true;
        } else {
            return super.onLeftClickEntity(stack, player, entity);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
        if (slot == EquipmentSlotType.MAINHAND) {
            map.put(Main.MAGICAL_DAMAGE, new AttributeModifier(MAGICAL_DAMAGE_UUID, "Weapon modifier", stack.getOrCreateTag().getInt("scol.Souls")+10, AttributeModifier.Operation.ADDITION));
            map.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.2F, AttributeModifier.Operation.ADDITION));
        }
        return map.build();
    }
    @Override
    public void onCraftedBy(ItemStack stack, World world, PlayerEntity player) {
        System.out.println(stack);
        System.out.println(world);
        System.out.println(player);
        super.onCraftedBy(stack, world, player);
    }
}
