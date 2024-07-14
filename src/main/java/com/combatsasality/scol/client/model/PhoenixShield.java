package com.combatsasality.scol.client.model;

import com.combatsasality.scol.Main;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class PhoenixShield extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Main.MODID, "textures/models/shieldtexture.png"), "shield");

    public PhoenixShield(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot().getChild("body");

        PartDefinition voxel_file = partdefinition.addOrReplaceChild("voxel_file", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0958F, 8.0F));

        PartDefinition bone = voxel_file.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(24, 16).addBox(-10.0F, -4.0F, 4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone2 = voxel_file.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(33, 20).addBox(-6.0F, -5.0F, 4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone3 = voxel_file.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(33, 11).addBox(-11.0F, -5.0F, 4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone4 = voxel_file.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(30, 0).addBox(-5.0F, -8.0F, 4.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone5 = voxel_file.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(28, 29).addBox(-12.0F, -8.0F, 4.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone6 = voxel_file.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(29, 21).addBox(-4.0F, -10.0F, 4.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone7 = voxel_file.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(25, 21).addBox(-13.0F, -11.0F, 4.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone8 = voxel_file.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(9, 21).addBox(-3.0F, -24.0F, 4.0F, 1.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone9 = voxel_file.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -25.0F, 4.0F, 4.0F, 21.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone10 = voxel_file.addOrReplaceChild("bone10", CubeListBuilder.create().texOffs(10, 0).addBox(-14.0F, -24.0F, 4.0F, 1.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone11 = voxel_file.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(17, 21).addBox(-2.0F, -23.0F, 4.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone12 = voxel_file.addOrReplaceChild("bone12", CubeListBuilder.create().texOffs(18, 0).addBox(-6.0F, -25.0F, 4.0F, 1.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone13 = voxel_file.addOrReplaceChild("bone13", CubeListBuilder.create().texOffs(14, 0).addBox(-11.0F, -25.0F, 4.0F, 1.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone14 = voxel_file.addOrReplaceChild("bone14", CubeListBuilder.create().texOffs(13, 21).addBox(-15.0F, -23.0F, 4.0F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone15 = voxel_file.addOrReplaceChild("bone15", CubeListBuilder.create().texOffs(26, 8).addBox(-1.0F, -15.0F, 4.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone16 = voxel_file.addOrReplaceChild("bone16", CubeListBuilder.create().texOffs(0, 22).addBox(-5.0F, -25.0F, 4.0F, 1.0F, 17.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone17 = voxel_file.addOrReplaceChild("bone17", CubeListBuilder.create().texOffs(21, 20).addBox(-12.0F, -25.0F, 4.0F, 1.0F, 17.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone18 = voxel_file.addOrReplaceChild("bone18", CubeListBuilder.create().texOffs(26, 0).addBox(-16.0F, -15.0F, 4.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone19 = voxel_file.addOrReplaceChild("bone19", CubeListBuilder.create().texOffs(22, 0).addBox(-4.0F, -25.0F, 4.0F, 1.0F, 15.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone20 = voxel_file.addOrReplaceChild("bone20", CubeListBuilder.create().texOffs(4, 22).addBox(-13.0F, -25.0F, 4.0F, 1.0F, 14.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone21 = voxel_file.addOrReplaceChild("bone21", CubeListBuilder.create().texOffs(32, 28).addBox(-1.0F, -20.0F, 4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone22 = voxel_file.addOrReplaceChild("bone22", CubeListBuilder.create().texOffs(30, 7).addBox(-16.0F, -20.0F, 4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone23 = voxel_file.addOrReplaceChild("bone23", CubeListBuilder.create().texOffs(33, 25).addBox(-1.0F, -22.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        PartDefinition bone24 = voxel_file.addOrReplaceChild("bone24", CubeListBuilder.create().texOffs(33, 6).addBox(-16.0F, -22.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 12.6042F, -2.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.skipDraw = true;
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }


    @Override
    @Nonnull
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body);
    }
}