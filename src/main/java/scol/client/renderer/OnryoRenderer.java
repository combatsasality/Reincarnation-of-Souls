package scol.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import scol.Main;
import scol.client.model.OnryoModel;
import scol.entity.Onryo;

@OnlyIn(Dist.CLIENT)
public class OnryoRenderer extends EntityRenderer<Onryo> {
    protected static final ResourceLocation TEXTURE_DEFAULT = new ResourceLocation(Main.modid, "textures/entity/onryo_default.png");
    protected static final ResourceLocation TEXTURE_FLY = new ResourceLocation(Main.modid, "textures/entity/onryo_fly.png");

    public OnryoRenderer(EntityRendererManager rendermanager) {
        super(rendermanager);
    }

    @Override
    public void render(Onryo entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light) {
        matrixStack.pushPose();
        if (entity.onryoType == Onryo.OnryoType.FLY) {
            OnryoModel.OnryoFlyModel<Onryo> model = new OnryoModel.OnryoFlyModel<>();
            model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityCutout(TEXTURE_FLY)),
                    light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            OnryoModel.OnryoDefaultModel<Onryo> model = new OnryoModel.OnryoDefaultModel<>();
            model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityCutout(TEXTURE_DEFAULT)),
                    light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Onryo p_110775_1_) {
        return null;
    }

}
