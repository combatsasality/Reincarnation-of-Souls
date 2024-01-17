package scol.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import scol.Main;
import scol.client.model.IchigoVizardModel;
import scol.entity.IchigoVizard;

@OnlyIn(Dist.CLIENT)
public class IchigoVizardRenderer extends MobRenderer<IchigoVizard, IchigoVizardModel<IchigoVizard>> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.modid, "textures/entity/ichigo_vizard.png");

    public IchigoVizardRenderer(EntityRendererManager rendermanager) {
        super(rendermanager, new IchigoVizardModel<>(), 1.0f);
        this.addLayer(new HeldItemLayer<>(this));
    }
    @Override
    public ResourceLocation getTextureLocation(IchigoVizard p_110775_1_) {
        return TEXTURE;
    }

}
