package scol.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class PhoenixShield extends BipedModel<LivingEntity> {
    private final ModelRenderer voxel_file;
    private final ModelRenderer bone;
    private final ModelRenderer bone2;
    private final ModelRenderer bone3;
    private final ModelRenderer bone4;
    private final ModelRenderer bone5;
    private final ModelRenderer bone6;
    private final ModelRenderer bone7;
    private final ModelRenderer bone8;
    private final ModelRenderer bone9;
    private final ModelRenderer bone10;
    private final ModelRenderer bone11;
    private final ModelRenderer bone12;
    private final ModelRenderer bone13;
    private final ModelRenderer bone14;
    private final ModelRenderer bone15;
    private final ModelRenderer bone16;
    private final ModelRenderer bone17;
    private final ModelRenderer bone18;
    private final ModelRenderer bone19;
    private final ModelRenderer bone20;
    private final ModelRenderer bone21;
    private final ModelRenderer bone22;
    private final ModelRenderer bone23;
    private final ModelRenderer bone24;

    public PhoenixShield() {
        super(1.0f, 0, 64,64);
        texWidth = 64;
        texHeight = 64;
        body = new ModelRenderer(this);
        head = new ModelRenderer(this);
        hat = new ModelRenderer(this);
        rightArm = new ModelRenderer(this);
        leftArm = new ModelRenderer(this);
        rightLeg = new ModelRenderer(this);
        leftLeg = new ModelRenderer(this);

        voxel_file = new ModelRenderer(this);
        voxel_file.setPos(0.0F, 3.0958F, 8.0F);
        body.addChild(voxel_file);

        bone = new ModelRenderer(this);
        bone.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone);
        bone.texOffs(24, 16).addBox(-10.0F, -4.0F, 4.0F, 4.0F, 4.0F, 1.0F, 0.0F, false);

        bone2 = new ModelRenderer(this);
        bone2.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone2);
        bone2.texOffs(33, 20).addBox(-6.0F, -5.0F, 4.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone3);
        bone3.texOffs(33, 11).addBox(-11.0F, -5.0F, 4.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        bone4 = new ModelRenderer(this);
        bone4.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone4);
        bone4.texOffs(30, 0).addBox(-5.0F, -8.0F, 4.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);

        bone5 = new ModelRenderer(this);
        bone5.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone5);
        bone5.texOffs(28, 29).addBox(-12.0F, -8.0F, 4.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);

        bone6 = new ModelRenderer(this);
        bone6.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone6);
        bone6.texOffs(29, 21).addBox(-4.0F, -10.0F, 4.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);

        bone7 = new ModelRenderer(this);
        bone7.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone7);
        bone7.texOffs(25, 21).addBox(-13.0F, -11.0F, 4.0F, 1.0F, 8.0F, 1.0F, 0.0F, false);

        bone8 = new ModelRenderer(this);
        bone8.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone8);
        bone8.texOffs(9, 21).addBox(-3.0F, -24.0F, 4.0F, 1.0F, 20.0F, 1.0F, 0.0F, false);

        bone9 = new ModelRenderer(this);
        bone9.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone9);
        bone9.texOffs(0, 0).addBox(-10.0F, -25.0F, 4.0F, 4.0F, 21.0F, 1.0F, 0.0F, false);

        bone10 = new ModelRenderer(this);
        bone10.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone10);
        bone10.texOffs(10, 0).addBox(-14.0F, -24.0F, 4.0F, 1.0F, 20.0F, 1.0F, 0.0F, false);

        bone11 = new ModelRenderer(this);
        bone11.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone11);
        bone11.texOffs(17, 21).addBox(-2.0F, -23.0F, 4.0F, 1.0F, 18.0F, 1.0F, 0.0F, false);

        bone12 = new ModelRenderer(this);
        bone12.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone12);
        bone12.texOffs(18, 0).addBox(-6.0F, -25.0F, 4.0F, 1.0F, 20.0F, 1.0F, 0.0F, false);

        bone13 = new ModelRenderer(this);
        bone13.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone13);
        bone13.texOffs(14, 0).addBox(-11.0F, -25.0F, 4.0F, 1.0F, 20.0F, 1.0F, 0.0F, false);

        bone14 = new ModelRenderer(this);
        bone14.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone14);
        bone14.texOffs(13, 21).addBox(-15.0F, -23.0F, 4.0F, 1.0F, 18.0F, 1.0F, 0.0F, false);

        bone15 = new ModelRenderer(this);
        bone15.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone15);
        bone15.texOffs(26, 8).addBox(-1.0F, -15.0F, 4.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);

        bone16 = new ModelRenderer(this);
        bone16.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone16);
        bone16.texOffs(0, 22).addBox(-5.0F, -25.0F, 4.0F, 1.0F, 17.0F, 1.0F, 0.0F, false);

        bone17 = new ModelRenderer(this);
        bone17.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone17);
        bone17.texOffs(21, 20).addBox(-12.0F, -25.0F, 4.0F, 1.0F, 17.0F, 1.0F, 0.0F, false);

        bone18 = new ModelRenderer(this);
        bone18.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone18);
        bone18.texOffs(26, 0).addBox(-16.0F, -15.0F, 4.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);

        bone19 = new ModelRenderer(this);
        bone19.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone19);
        bone19.texOffs(22, 0).addBox(-4.0F, -25.0F, 4.0F, 1.0F, 15.0F, 1.0F, 0.0F, false);

        bone20 = new ModelRenderer(this);
        bone20.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone20);
        bone20.texOffs(4, 22).addBox(-13.0F, -25.0F, 4.0F, 1.0F, 14.0F, 1.0F, 0.0F, false);

        bone21 = new ModelRenderer(this);
        bone21.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone21);
        bone21.texOffs(32, 28).addBox(-1.0F, -20.0F, 4.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        bone22 = new ModelRenderer(this);
        bone22.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone22);
        bone22.texOffs(30, 7).addBox(-16.0F, -20.0F, 4.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        bone23 = new ModelRenderer(this);
        bone23.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone23);
        bone23.texOffs(33, 25).addBox(-1.0F, -22.0F, 4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        bone24 = new ModelRenderer(this);
        bone24.setPos(8.0F, 12.6042F, -2.5F);
        voxel_file.addChild(bone24);
        bone24.texOffs(33, 6).addBox(-16.0F, -22.0F, 4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}

