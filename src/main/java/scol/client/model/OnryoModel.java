package scol.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import scol.entity.Onryo;

public class OnryoModel {
    public static class OnryoDefaultModel<T extends Onryo> extends BipedModel<T> {
        private final ModelRenderer bb_main;

        public OnryoDefaultModel() {
            super(1.0F);
            texWidth = 16;
            texHeight = 20;

            bb_main = new ModelRenderer(this);
            bb_main.setPos(0.0F, 24.0F, 0.0F);
            bb_main.texOffs(0, 0).addBox(-6.0F, -17.0F, -1.0F, 13.0F, 17.0F, 2.0F, 0.0F, false);
        }
        @Override
        public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {

        }
        @Override
        public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
            bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
        public void setRotationAngle(float x, float y) {
            this.bb_main.xRot = (float) (x * Math.PI / 180F);
            this.bb_main.yRot = (float) -(y * Math.PI / 180F);

        }

    }
    public static class OnryoFlyModel<T extends Onryo> extends BipedModel<T> {
        private final ModelRenderer bb_main;
        public OnryoFlyModel() {
            super(1.0F);
            texWidth = 16;
            texHeight = 20;

            bb_main = new ModelRenderer(this);
            bb_main.setPos(0.0F, 24.0F, 0.0F);
            bb_main.texOffs(0, 0).addBox(-7.0F, -18.0F, -1.0F, 14.0F, 18.0F, 2.0F, 0.0F, false);
        }

        @Override
        public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {

        }

        @Override
        public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
            bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        public void setRotationAngle(float x, float y) {
            this.bb_main.xRot = (float) (x * Math.PI / 180F);
            this.bb_main.yRot = (float) -(y * Math.PI / 180F);

        }


    }
}
