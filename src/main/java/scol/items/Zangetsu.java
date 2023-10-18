package scol.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import scol.Main;
import scol.handlers.ItemTier;

public class Zangetsu extends SwordItem {
    public Zangetsu() {
        super(ItemTier.FOR_ALL, 39, 0, new Properties().tab(Main.TAB).fireResistant());
        this.setRegistryName("zangetsu");
    }
    public static boolean isBankai(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.bankai") || stack.getOrCreateTag().getInt("scol.bankai_time") != 0;
    }
    public static void setBankai(ItemStack stack, boolean b) {
        stack.getOrCreateTag().putBoolean("scol.bankai", b);
    }
    public static int getBankaiTime(ItemStack stack) {
        return stack.getOrCreateTag().getInt("scol.bankai_time");
    }
    public static void setBankaiTime(ItemStack stack, int i) {
        stack.getOrCreateTag().putInt("scol.bankai_time", i);
    }
    public static boolean isDeathModel(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.death");
    }
    public static void setDeathModel(ItemStack stack, boolean b) {
        stack.getOrCreateTag().putBoolean("scol.death", b);
    }
    public static boolean isDisableGravity(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.disableGravity");
    }
    public static void setDisableGravity(ItemStack stack, boolean b) {
        stack.getOrCreateTag().putBoolean("scol.disableGravity", b);
    }

    public static String getOwner(ItemStack stack) {
        return stack.getOrCreateTag().getString("scol.owner");
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

}
