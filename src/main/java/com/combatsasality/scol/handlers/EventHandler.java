package com.combatsasality.scol.handlers;

import com.combatsasality.scol.registries.ScolItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler {

    @SubscribeEvent
    public void FrostmourneKill(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            ItemStack stack = player.getMainHandItem();
            CompoundTag tag = stack.getOrCreateTag();
            if (stack.getItem().equals(ScolItems.FROSTMOURNE) && tag.getInt("scol.Souls") < 100) {
                tag.putInt("scol.Souls", tag.getInt("scol.Souls") + 1);
            }
        }

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void ToolTipForMagicalDamage(ItemTooltipEvent event) { //It's a bullshit code, so what? If you know how to do it in a non-bullshit way, write me :D
        int index = 0;
        if (!event.getItemStack().getItem().equals(ScolItems.FROSTMOURNE)) return;
        for (Component text : event.getToolTip()) {
            index++;
            if (!(text instanceof MutableComponent mutableComponent)) continue;
            if (!(mutableComponent.getContents() instanceof TranslatableContents translatableContents)) continue;
            if (!translatableContents.getKey().equalsIgnoreCase("attribute.modifier.plus.0")) continue;
            if (!(translatableContents.getArgs()[1] instanceof MutableComponent magicalComponent)) continue;
            if (!(magicalComponent.getContents() instanceof TranslatableContents magicalTranslatable)) continue;
            if (!magicalTranslatable.getKey().equalsIgnoreCase("attribute.name.scol.magical_damage")) continue;


            Object[] objects = new Object[]{translatableContents.getArgs()[0], translatableContents.getArgs()[1]};
            MutableComponent newComponent = Component.translatable("attribute.modifier.equals.0", objects);
            newComponent.withStyle(ChatFormatting.DARK_PURPLE);
            event.getToolTip().set(index-1, newComponent);

        }

    }

}
