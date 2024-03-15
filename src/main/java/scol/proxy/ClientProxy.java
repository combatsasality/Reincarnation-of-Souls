package scol.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import scol.client.renderer.*;
import scol.items.PhoenixRing;
import scol.items.Zangetsu;
import scol.registries.ScolBlocks;
import scol.registries.ScolEntities;
import scol.registries.ScolItems;

import java.util.Map;

public class ClientProxy extends CommonProxy{
    public ClientProxy () {
        super();
    }
    @Override
    public void initAuxiliaryRender() {
        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
        for (PlayerRenderer render : skinMap.values()) {
            render.addLayer(new ShieldLayer<>(render));
        }
    }
    @Override
    public void initEntityRendering() {
        RenderingRegistry.registerEntityRenderingHandler(ScolEntities.CUSTOM_ITEM_ENTITY_ENT, renderManager -> new CustomItemEntityRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ScolEntities.ICHIGO_VIZARD, IchigoVizardRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ScolEntities.POWER_WAVE, PowerWaveRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ScolEntities.ONRYO, OnryoRenderer::new);
    }

    @Override
    public void initPropertyOverrideRegistry(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemModelsProperties.register(ScolItems.PHOENIX_RING.getItem(), new ResourceLocation("scol:chick"), (itemStack, clientWorld, livingEntity) -> PhoenixRing.getFloatForChickRing(itemStack));
            ItemModelsProperties.register(ScolItems.ZANGETSU.getItem(), new ResourceLocation("scol:zangetsu_model"), (itemStack, clientWorld, livingEntity) -> Zangetsu.getZangetsuModel(itemStack));
        });
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        this.initAuxiliaryRender();
    }

    @Override
    public void setupClient(FMLCommonSetupEvent event) {
        // wtf forge?
        RenderTypeLookup.setRenderLayer(ScolBlocks.SOUL_GLASS.getBlock(), RenderType.translucent());
    }
}
