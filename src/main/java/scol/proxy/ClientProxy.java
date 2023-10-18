package scol.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import scol.client.renderer.CustomItemEntityRenderer;
import scol.client.renderer.IchigoVazardRenderer;
import scol.client.renderer.ShieldLayer;
import scol.entity.CustomItemEntity;
import scol.entity.IchigoVazard;

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
        RenderingRegistry.registerEntityRenderingHandler(IchigoVazard.TYPE, IchigoVazardRenderer::new);
    }
    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        this.initAuxiliaryRender();
    }
}
