package scol.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import scol.Main;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class InactivePhoenixRing extends Item implements ICurioItem {
    public InactivePhoenixRing() {
        super(new Properties().tab(Main.TAB).stacksTo(1));
        this.setRegistryName("phoenix_ring_inactive");
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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();
        map.put(Attributes.MAX_HEALTH, new AttributeModifier(uuid, "remove_health_inphoenixring", -1.0F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return map;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canUnequip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return livingEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity) livingEntity).isCreative() || livingEntity instanceof ServerPlayerEntity && CuriosApi.getCuriosHelper().findFirstCurio(livingEntity, Main.phoenixRing).isPresent();
    }

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(LivingEntity livingEntity, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }
}
