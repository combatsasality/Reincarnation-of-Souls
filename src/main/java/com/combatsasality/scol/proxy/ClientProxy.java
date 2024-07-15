package com.combatsasality.scol.proxy;

import com.combatsasality.scol.client.renderer.CustomItemRenderer;
import com.combatsasality.scol.client.renderer.IchigoVizardRenderer;
import com.combatsasality.scol.client.renderer.PowerWaveRenderer;
import com.combatsasality.scol.client.renderer.ShieldLayer;
import com.combatsasality.scol.registries.ScolEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        super();
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void addLayers(EntityRenderersEvent.AddLayers event) {
        for (String skinType : event.getSkins()) {
            LivingEntityRenderer<?, ?> renderer = event.getSkin(skinType);

            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new ShieldLayer<>(playerRenderer));
            }
        }
    }

    @Override
    public void initEntityRendering() {
        EntityRenderers.register(ScolEntities.ICHIGO_VAZARD, IchigoVizardRenderer::new);
        EntityRenderers.register(ScolEntities.CUSTOM_ITEM_ENTITY, renderManager -> new CustomItemRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
        EntityRenderers.register(ScolEntities.POWER_WAVE_ENTITY, PowerWaveRenderer::new);
    }
}
