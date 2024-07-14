package com.combatsasality.scol.client.model;

import com.combatsasality.scol.entity.IchigoVizard;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class IchigoVizardModel<T extends IchigoVizard> extends HumanoidModel<T> {

    public IchigoVizardModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createModel() {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshDefinition, 64, 64);
    }


    @Override
    public void setupAnim(T entity, float f, float f1, float f2, float f3, float f4) {
        this.rightArm.xRot = Mth.cos(f * 0.6662F) * f1;
        this.leftArm.xRot = Mth.cos(f * 0.6662F + (float) Math.PI) * f1;
        this.rightLeg.xRot = Mth.cos(f * 1.0F) * 1.0F * f1;
        this.leftLeg.xRot = Mth.cos(f * 1.0F) * -1.0F * f1;
        this.rightArm.yRot = 0;
        this.rightArm.zRot = 0;
        this.leftArm.yRot = 0;
        this.leftArm.zRot = 0;
        this.setupAttackAnimation(entity, f2);
    }


    @Override
    public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {
        this.getArm(p_102854_).translateAndRotate(p_102855_);
    }

    @Override
    protected ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }
}
