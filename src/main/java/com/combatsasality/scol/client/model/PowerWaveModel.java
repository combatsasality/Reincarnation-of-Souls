package com.combatsasality.scol.client.model;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.entity.projectile.PowerWaveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class PowerWaveModel extends EntityModel<PowerWaveEntity> {
    private final ModelPart wave;

    public PowerWaveModel(ModelPart root) {
        this.wave = root.getChild("wave");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("wave", CubeListBuilder.create().texOffs(-39, 0).addBox(-49.0F, 0.0F, -27.0F, 98.0F, 0.0F, 41.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 200, 40);
    }

    @Override
    public void setupAnim(PowerWaveEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        wave.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(float x, float y) {
        this.wave.xRot = (float) (x * Math.PI / 180F);
        this.wave.yRot = (float) -(y * Math.PI / 180F);

    }

}