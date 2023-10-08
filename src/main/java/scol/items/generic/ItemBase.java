package scol.items.generic;

import net.minecraft.item.Item;
import scol.Main;

public class ItemBase extends Item {
    public ItemBase(String regitem, Properties properties) {
        super(properties.tab(Main.TAB));
        this.setRegistryName(regitem);
    }

    public ItemBase(String regitem) {
        super(new Properties().tab(Main.TAB));
        this.setRegistryName(regitem);
    }

}
