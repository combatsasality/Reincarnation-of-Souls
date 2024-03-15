package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.entity.IchigoVizard;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class SummonMask extends Item {


    public SummonMask() {
        super(new Properties().tab(Main.TAB));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        boolean summoned = context.getLevel().addFreshEntity(new IchigoVizard(context.getLevel(), context.getClickLocation()));
        if (summoned) {
            context.getItemInHand().setCount(context.getItemInHand().getCount()-1);
        }
        return super.useOn(context);
    }
}
