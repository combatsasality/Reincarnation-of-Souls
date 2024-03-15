package scol.items.generic;

import net.minecraft.item.Item;
import scol.Main;

public class ItemBase extends Item {
    public ItemBase(String regItem, Properties properties) {
        super(properties.tab(Main.TAB));
        this.setRegistryName(regItem);
    }

    public ItemBase(String regItem) {
        super(new Properties().tab(Main.TAB));
        this.setRegistryName(regItem);
    }

    public ItemBase(Properties properties) {
        super(properties.tab(Main.TAB));
    }

    public ItemBase() {
        super(new Properties().tab(Main.TAB));
    }

}
