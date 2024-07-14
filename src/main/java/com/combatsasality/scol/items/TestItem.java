package com.combatsasality.scol.items;

import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolSounds;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestItem extends Item {
    public TestItem() {
        super(new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.item.test_item.0"));

        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            player.setHealth(player.getMaxHealth());
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1000, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 1, true, false));

        }
        super.inventoryTick(stack, level, entity, p_41407_, p_41408_);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        context.getLevel().playSound(null, context.getClickedPos(), ScolSounds.MUSIC_METAL_3, context.getPlayer().getSoundSource(), 1.0F, 1.0F);
        return super.useOn(context);
    }
}
