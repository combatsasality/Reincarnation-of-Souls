package com.combatsasality.scol.items;

import com.combatsasality.scol.capabilities.AcademyCapability;
import com.combatsasality.scol.handlers.Academy;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolCapabilities;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

public class AcademyBook extends Item implements ITab {
    private Academy academy;

    public AcademyBook(Academy academy) {
        super(new Properties().stacksTo(1));
        this.academy = academy;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            LazyOptional<AcademyCapability.IAcademyCapability> capability =
                    player.getCapability(ScolCapabilities.ACADEMY_CAPABILITY);
            Academy academy = capability.map((capa) -> capa.getAcademy()).orElse(Academy.NULL);

            if (academy.equals(Academy.NULL)) {
                capability.ifPresent((capa) -> capa.setAcademy(this.academy));
                player.getItemInHand(hand).shrink(1);
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }
}
