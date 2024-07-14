package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.handlers.ItemTier;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolTabs;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Zangetsu extends SwordItem implements ITab {
    public Zangetsu() {
        super(ItemTier.FOR_ALL, 39, 0, new Properties().fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getHoverName().getString().equals("combatsasality")) {
            tooltip.add(Component.translatable("tooltip.scol.zangetsu.combatsasality"));
        }
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }

    @Override
    public List<ItemStack> getCreativeTabStacks() {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putString("scol.Owner", Minecraft.getInstance().player.getGameProfile().getName());
        return ImmutableList.of(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.isEnchanted();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }





    public static String getOwner(ItemStack stack) {
        return stack.getOrCreateTag().getString("scol.Owner");
    }
    public static boolean isBankai(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.Bankai");
    }
    public static void setBankai(ItemStack stack, boolean bankai) {
        stack.getOrCreateTag().putBoolean("scol.Bankai", bankai);
    }
    public static boolean isDeathModel(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.Death");
    }
    public static boolean isDisableGravity(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.DisableGravity");
    }
    public static void setDisableGravity(ItemStack stack, boolean disableGravity) {
        stack.getOrCreateTag().putBoolean("scol.DisableGravity", disableGravity);
    }

    public static int getZangetsuModel(ItemStack stack) {
        boolean isDeathModel = isDeathModel(stack);
        boolean isBankai = isBankai(stack);
        String hoverName = stack.getHoverName().getString();

        if (isBankai && !isDeathModel) {
            return 6;
        }
        if (hoverName.equalsIgnoreCase("\u0434\u0436\u0443\u043C\u0430\u043C\u0431\u0435\u0430\u0431\u0441") && !isDeathModel) {
            return 4;
        }
        if (hoverName.equalsIgnoreCase("\u0434\u0436\u0443\u043C\u0430\u043C\u0431\u0435\u0430\u0431\u0441")) {
            return 5;
        }
        if (hoverName.equalsIgnoreCase("combatsasality") && isDeathModel) {
            return 3;
        }
        if (hoverName.equalsIgnoreCase("combatsasality")) {
            return 2;
        }
        if (isDeathModel) {
            return 1;
        }
        return 0;
    }

    public void registerVariants() {
        ItemProperties.register(this, new ResourceLocation(Main.MODID, "zangetsu_model"), (stack, world, entity, number) -> getZangetsuModel(stack));
    }
}
