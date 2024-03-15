package scol.items;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import scol.Main;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WorldWing extends Item implements ICurioItem {
    public WorldWing() {
        super(new Properties().tab(Main.TAB).stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag tooltip) {
        list.add(new TranslationTextComponent("tooltip.scol.world_wing.2", stack.getOrCreateTag().getInt("scol.Speed")).withStyle(TextFormatting.LIGHT_PURPLE));
        list.add(new TranslationTextComponent("tooltip.scol.currentKeybind", KeyBinding.createNameSupplier("key.world_wing").get().getString().toUpperCase()).withStyle().withStyle(TextFormatting.LIGHT_PURPLE));
        list.add(new TranslationTextComponent("tooltip.scol.empty"));

        if (Screen.hasShiftDown()) {
            list.add(new TranslationTextComponent("tooltip.scol.world_wing.0"));
            list.add(new TranslationTextComponent("tooltip.scol.world_wing.1"));
        } else {
            list.add(new TranslationTextComponent("tooltip.scol.hold_shift"));
        }
        super.appendHoverText(stack, world, list, tooltip);
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity entity, ItemStack stack){
        return true;
    }

    @Override
    public boolean canUnequip(String identifier, LivingEntity entity, ItemStack stack){
        return ((PlayerEntity) entity).isCreative();
    }
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    @Nonnull
    public ICurio.DropRule getDropRule(LivingEntity livingEntity, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    public static int getFlySpeedInt(ItemStack stack) {return stack.getOrCreateTag().getInt("scol.Speed");}

    public static void setFlySpeed(ItemStack stack,int speed) {
        stack.getOrCreateTag().putInt("scol.Speed", speed);
    }
    public static float getFlySpeed(ItemStack stack) {
        return stack.getOrCreateTag().getInt("scol.Speed")*0.1F+0.05F;
    }
    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
        if (!player.abilities.mayfly) {
            player.abilities.mayfly = true;
            player.onUpdateAbilities();
        }
        if (player.abilities.flyingSpeed != getFlySpeed(stack)) {
            player.abilities.flyingSpeed = getFlySpeed(stack);
            player.abilities.flying = true;
            player.onUpdateAbilities();
        }
        ICurioItem.super.curioTick(identifier, index, livingEntity, stack);
    }

}
