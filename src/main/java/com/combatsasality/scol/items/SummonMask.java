package com.combatsasality.scol.items;

import com.combatsasality.scol.entity.IchigoVizard;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class SummonMask extends Item implements ITab {
    public SummonMask() {
        super(new Properties());
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        boolean summoned =
                context.getLevel().addFreshEntity(new IchigoVizard(context.getLevel(), context.getClickLocation()));
        if (summoned) {
            context.getItemInHand().shrink(1);
        }

        return super.useOn(context);
    }
}
