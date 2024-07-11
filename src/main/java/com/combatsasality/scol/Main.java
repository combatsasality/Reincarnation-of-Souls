package com.combatsasality.scol;

import com.combatsasality.scol.handlers.EventHandler;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolAttributes;
import com.combatsasality.scol.registries.ScolItems;
import com.combatsasality.scol.registries.ScolLootModifiers;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "scol";
    public static final String VERSION = "1.0";
    public static final String VERSION_MINECRAFT = "1.16.5";
    private static final String PTC_VERSION = "1";


    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        new ScolTabs();
        new ScolItems();
        new ScolAttributes();
        new ScolLootModifiers();
    }

    @SubscribeEvent
    public void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof ITab member) {
                if (event.getTab() != member.getCreativeTab()) return;

                member.getCreativeTabStacks().forEach(event::accept);
            }
        });
    }
    @SubscribeEvent
    public void addAttribute(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ScolAttributes.MAGICAL_DAMAGE);
    }

}
