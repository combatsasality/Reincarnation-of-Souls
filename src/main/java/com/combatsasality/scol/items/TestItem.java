package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.packets.server.PacketGetCapability;
import com.combatsasality.scol.registries.ScolCapabilities;
import com.combatsasality.scol.registries.ScolSounds;
import com.combatsasality.scol.registries.ScolTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestItem extends Item {
    public TestItem() {
        super(new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.item.test_item.0"));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.literal(Minecraft.getInstance().player.getCapability(ScolCapabilities.SCOL_CAPABILITY).map(capa -> capa.writeTag()).orElse(null).toString()));
        }
        Main.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketGetCapability(true));

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
        context.getPlayer().getCapability(ScolCapabilities.SCOL_CAPABILITY).ifPresent(capa -> {
            capa.setActiveBankaiTime(0);
            capa.raiseLevelBankai();
            capa.raiseLevelBankai();
            capa.raiseLevelBankai();
            capa.raiseLevelBankai();
            capa.raiseLevelBankai();
            capa.setCooldownBankai(0);
        });
        return super.useOn(context);
    }
}
