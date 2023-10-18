package scol.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import scol.Main;
import scol.client.model.IchigoVazardModel;
import scol.entity.IchigoVazard;

@OnlyIn(Dist.CLIENT)
public class IchigoVazardRenderer extends MobRenderer<IchigoVazard, IchigoVazardModel<IchigoVazard>> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.modid, "textures/entity/ichigo_vazard.png");

    public IchigoVazardRenderer(EntityRendererManager rendermanager) {
        super(rendermanager, new IchigoVazardModel<>(), 1.0f);
        this.addLayer(new HeldItemLayer<>(this));
    }
    @Override
    public ResourceLocation getTextureLocation(IchigoVazard p_110775_1_) {
        return TEXTURE;
    }

}
