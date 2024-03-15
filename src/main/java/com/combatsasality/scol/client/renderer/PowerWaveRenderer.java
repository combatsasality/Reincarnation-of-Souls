package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.client.model.PowerWaveModel;
import com.combatsasality.scol.entity.projectile.PowerWaveEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class PowerWaveRenderer extends EntityRenderer<PowerWaveEntity> {
        protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/power_wave.png");

    public PowerWaveRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(PowerWaveEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light) {
        matrixStack.pushPose();
        PowerWaveModel model = new PowerWaveModel();
        model.setRotationAngle(entity.xRot, entity.yRot);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityCutout(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);


        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PowerWaveEntity p_110775_1_) {
        return null;
    }

}
