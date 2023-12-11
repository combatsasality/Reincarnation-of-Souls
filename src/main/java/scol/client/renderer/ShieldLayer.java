package scol.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import scol.Main;
import scol.client.model.PhoenixShield;
import scol.items.PhoenixRing;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;

@OnlyIn(Dist.CLIENT)
public class ShieldLayer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T,M> {
    public ShieldLayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CuriosApi.getCuriosHelper().findFirstCurio(livingEntity, Main.phoenixRing.getItem()).ifPresent(slot -> {
            if (PhoenixRing.godModeIsActive(slot.getStack())) {
                matrixStack.pushPose();
                BipedModel<LivingEntity> model = new PhoenixShield();
                model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
                ICurio.RenderHelper.followBodyRotations(livingEntity, model);
                model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation(Main.modid, "textures/items/models/shieldtexture.png"))),
                        light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStack.popPose();
            }
        });
    }
}