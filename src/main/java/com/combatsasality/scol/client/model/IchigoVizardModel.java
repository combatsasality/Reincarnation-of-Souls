package com.combatsasality.scol.client.model;

import com.combatsasality.scol.entity.IchigoVizard;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class IchigoVizardModel<T extends IchigoVizard> extends HumanoidModel<T> {

    private final ModelPart horn;
    private final ModelPart secondHorn;

    public IchigoVizardModel(ModelPart root) {
        super(root);
        this.horn = root.getChild("horn");
        this.secondHorn = root.getChild("secondHorn");
    }

    public static LayerDefinition createModel() {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshDefinition.getRoot();

        PartDefinition horn = partdefinition.addOrReplaceChild("horn", CubeListBuilder.create().texOffs(2, 43).addBox(-1.3317F, -1.4324F, -2.1956F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(3, 44).addBox(-0.5317F, -1.2324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 44).addBox(-0.5317F, -0.6324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.0001F))
                .texOffs(3, 44).addBox(-1.1317F, -1.2324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0001F))
                .texOffs(3, 44).addBox(-0.8317F, -0.9324F, -4.0456F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0001F))
                .texOffs(3, 44).addBox(-1.1317F, -0.6324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5154F, -7.7799F, -8.4242F));

        PartDefinition cube_r1 = horn.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(2, 43).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3317F, -0.4324F, -0.1956F, 0.0F, 0.0F, 0.0266F));

        PartDefinition cube_r2 = horn.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(2, 43).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2723F, -0.1751F, 1.4265F, -0.3196F, 0.0F, 0.0702F));

        PartDefinition cube_r3 = horn.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(1, 42).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8784F, 0.4442F, 2.9509F, -0.4551F, 0.0F, -0.2461F));

        PartDefinition cube_r4 = horn.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 41).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7846F, 1.0288F, 4.4936F, -0.3298F, 0.0F, 0.0F));

        PartDefinition cube_r5 = horn.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 43).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5154F, 1.7799F, 6.8242F, -0.3054F, 0.0F, 0.0F));

        PartDefinition secondHorn = partdefinition.addOrReplaceChild("secondHorn", CubeListBuilder.create().texOffs(2, 43).mirror().addBox(-0.6683F, -1.4324F, -2.1956F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(3, 44).mirror().addBox(-0.4683F, -1.2324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(3, 44).mirror().addBox(-0.4683F, -0.6324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.0001F)).mirror(false)
                .texOffs(3, 44).mirror().addBox(0.1317F, -1.2324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0001F)).mirror(false)
                .texOffs(3, 44).mirror().addBox(-0.1683F, -0.9324F, -4.0456F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0001F)).mirror(false)
                .texOffs(3, 44).mirror().addBox(0.1317F, -0.6324F, -3.1956F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.5154F, -7.7799F, -8.4242F));

        PartDefinition cube_r6 = secondHorn.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(2, 43).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.3317F, -0.4324F, -0.1956F, 0.0F, 0.0F, -0.0266F));

        PartDefinition cube_r7 = secondHorn.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(2, 43).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.2723F, -0.1751F, 1.4265F, -0.3196F, 0.0F, -0.0702F));

        PartDefinition cube_r8 = secondHorn.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(1, 42).mirror().addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.8784F, 0.4442F, 2.9509F, -0.4551F, 0.0F, 0.2461F));

        PartDefinition cube_r9 = secondHorn.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 41).mirror().addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.7846F, 1.0288F, 4.4936F, -0.3298F, 0.0F, 0.0F));

        PartDefinition cube_r10 = secondHorn.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 43).mirror().addBox(-2.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5154F, 1.7799F, 6.8242F, -0.3054F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
        this.rightArm.yRot = 0;
        this.rightArm.zRot = 0;
        this.leftArm.yRot = 0;
        this.leftArm.zRot = 0;
        this.setupAttackAnimation(entity, limbSwingAmount);

        // guard: the sword arm comes up and across the body, blade in front of the face, the off hand braces it.
        // Blended over the walk/attack animation so the arms visibly fold into the stance and unfold after it.
        float guard = entity.getCounterBlend(ageInTicks);
        if (guard > 0.0F) {
            this.rightArm.xRot = Mth.lerp(guard, this.rightArm.xRot, -1.9F);
            this.rightArm.yRot = Mth.lerp(guard, this.rightArm.yRot, -0.7F);
            this.rightArm.zRot = Mth.lerp(guard, this.rightArm.zRot, 0.35F);
            this.leftArm.xRot = Mth.lerp(guard, this.leftArm.xRot, -1.1F);
            this.leftArm.yRot = Mth.lerp(guard, this.leftArm.yRot, 0.6F);
            this.leftArm.zRot = Mth.lerp(guard, this.leftArm.zRot, -0.2F);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        horn.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        secondHorn.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}