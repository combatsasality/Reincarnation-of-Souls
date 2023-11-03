package scol.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import scol.Main;
import scol.entity.IchigoVazard;

public class SummonMask extends Item {


    public SummonMask() {
        super(new Properties().tab(Main.TAB));
        this.setRegistryName("summon_mask");
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        boolean summoned = context.getLevel().addFreshEntity(new IchigoVazard(context.getLevel(), context.getClickLocation()));
        if (summoned) {
            context.getItemInHand().setCount(context.getItemInHand().getCount()-1);
        }
        return super.useOn(context);
    }
}
