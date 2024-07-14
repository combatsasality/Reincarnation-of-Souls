package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.client.model.IchigoVizardModel;
import com.combatsasality.scol.entity.IchigoVizard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IchigoVizardRenderer extends MobRenderer<IchigoVizard, IchigoVizardModel<IchigoVizard>> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/ichigo_vizard.png");

    public IchigoVizardRenderer(EntityRendererProvider.Context context) {
        super(context, new IchigoVizardModel<>(IchigoVizardModel.createModel().bakeRoot()), 1.0f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }


    @Override
    public ResourceLocation getTextureLocation(IchigoVizard p_114482_) {
        return TEXTURE;
    }
}
