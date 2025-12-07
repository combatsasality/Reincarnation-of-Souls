package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.client.model.PowerWaveModel;
import com.combatsasality.scol.entity.projectile.PowerWaveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PowerWaveRenderer extends EntityRenderer<PowerWaveEntity> {
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(Main.MODID, "textures/entity/power_wave.png");

    public PowerWaveRenderer(EntityRendererProvider.Context rendererProvider) {
        super(rendererProvider);
    }

    @Override
    public void render(
            PowerWaveEntity entity,
            float p_114486_,
            float p_114487_,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light) {
        poseStack.pushPose();
        PowerWaveModel model =
                new PowerWaveModel(PowerWaveModel.createBodyLayer().bakeRoot());
        model.setRotationAngle(entity.getXRot(), entity.getYRot());
        model.renderToBuffer(
                poseStack,
                multiBufferSource.getBuffer(RenderType.entityCutout(TEXTURE)),
                light,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                1.0F);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PowerWaveEntity p_114482_) {
        return null;
    }
}
