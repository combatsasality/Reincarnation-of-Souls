package com.combatsasality.scol.client.model;

import com.combatsasality.scol.entity.projectile.PowerWaveEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PowerWaveModel extends EntityModel<PowerWaveEntity> {
    private final ModelRenderer wave;

    public PowerWaveModel() {
        texWidth = 200;
        texHeight = 40;

        wave = new ModelRenderer(this);
        wave.setPos(0.0F, 0.0F, 0.0F);
        wave.texOffs(-39, 0).addBox(-49.0F, 0.0F, -27.0F, 98.0F, 0.0F, 41.0F, 0.0F, false);
    }


    @Override
    public void setupAnim(PowerWaveEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){

    }
    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        this.wave.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

    }

    public void setRotationAngle(float x, float y) {
        this.wave.xRot = (float) (x * Math.PI / 180F);
        this.wave.yRot = (float) -(y * Math.PI / 180F);

    }



}