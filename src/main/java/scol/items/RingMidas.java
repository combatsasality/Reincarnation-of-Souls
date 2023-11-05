package scol.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.TextFormatting;
import scol.Main;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RingMidas extends Item implements ICurioItem {

    public RingMidas() {
        super(new Properties().stacksTo(1).tab(Main.TAB).rarity(Rarity.create("GOLD", TextFormatting.GOLD)));
        this.setRegistryName("ring_midas");
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
