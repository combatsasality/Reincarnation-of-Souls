package scol.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import scol.client.renderer.*;
import scol.entity.CustomItemEntity;
import scol.entity.IchigoVizard;
import scol.entity.Onryo;
import scol.entity.projectile.PowerWaveEntity;

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
        RenderingRegistry.registerEntityRenderingHandler(CustomItemEntity.TYPE, renderManager -> new CustomItemEntityRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(IchigoVizard.TYPE, IchigoVizardRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(PowerWaveEntity.TYPE, PowerWaveRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Onryo.TYPE, OnryoRenderer::new);
    }
    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        this.initAuxiliaryRender();
    }
}
