package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.client.model.PhoenixShield;
import com.combatsasality.scol.items.PhoenixRing;
import com.combatsasality.scol.registries.ScolItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class ShieldLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final PhoenixShield model;


    public ShieldLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);

        model = new PhoenixShield(PhoenixShield.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packetLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CuriosApi.getCuriosInventory(livingEntity).ifPresent(inventory -> {
            inventory.findFirstCurio(ScolItems.PHOENIX_RING).ifPresent(curio -> {
                if (PhoenixRing.godModeIsActive(curio.stack())) {
                    stack.pushPose();
                    ICurioRenderer.followBodyRotations(livingEntity, model);
                    model.renderToBuffer(stack, buffer.getBuffer(model.renderType(PhoenixShield.LAYER_LOCATION.getModel())), packetLight, LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                    stack.popPose();
                }
            });
        });
    }
}
