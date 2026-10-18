package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.client.model.IchigoVizardModel;
import com.combatsasality.scol.entity.IchigoVizard;
import com.combatsasality.scol.handlers.CeroHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class IchigoVizardRenderer extends MobRenderer<IchigoVizard, IchigoVizardModel<IchigoVizard>> {
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(Main.MODID, "textures/entity/ichigo_vizard.png");

    public IchigoVizardRenderer(EntityRendererProvider.Context context) {
        super(context, new IchigoVizardModel<>(IchigoVizardModel.createModel().bakeRoot()), 1.0f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

@Override
public void render(IchigoVizard entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

    LivingEntity target = entity.getSyncedTarget();

    if (entity.getStage().equals(IchigoVizard.Stage.RAGE) && target != null) {
        float[] colors = {0.0F, 0.0F, 0.0F};       // inner: black
        float[] outerColors = {0.85F, 0.0F, 0.0F}; // glow: red
        float beamStart = 2.05F;
        float maxLength = 1024.0F;

        // beam size
        float beamRadius = 0.5F;
        float glowRadius = 0.55F;

        // cube size at full charge
        float cubeSize = 0.4F;
        float cubeGlowSize = 0.45F;

        // ===== Timeline (in ticks) =====
        float chargeTicks = 120.0F; // cube grows
        float fireTicks = 20.0F;    // beam shoots out
        float holdTicks = 30.0F;   // beam holds
        float fadeTicks = 10.0F;   // beam and cube fade
        float pauseTicks = 80.0F;  // nothing (placeholder loop only)

        float shotTicks = fireTicks + holdTicks + fadeTicks;
        float cycleTicks = chargeTicks + shotTicks + pauseTicks;

        // Placeholder DELETE! loops the whole attack
        float t = (entity.tickCount + partialTick) % cycleTicks;

        if (t >= chargeTicks + shotTicks) {
            return; // pause
        }

        // ===== Aim (pitch only, beam stays in front of the face) =====
        float bodyYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float pitch = 0.0F;


        Vec3 origin = entity.getPosition(partialTick)
                .add(new Vec3(0.0D, beamStart, 0.80D).yRot(-bodyYaw * Mth.DEG_TO_RAD));
        Vec3 targetCenter = target.getPosition(partialTick).add(0.0D, target.getBbHeight() / 2.0D, 0.0D);
        Vec3 dir = targetCenter.subtract(origin);

        // vertical angle to the target: positive = above
        pitch = (float) (Mth.atan2(dir.y, dir.horizontalDistance()) * Mth.RAD_TO_DEG);
        pitch = Mth.clamp(pitch, -60.0F, 60.0F); // keep the beam in front of the face


        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.translate(0.0F, 0.0F, -0.80F);
        poseStack.rotateAround(Axis.XP.rotationDegrees(-90.0F + pitch), 0.0F, beamStart, 0.0F);

        float time = entity.tickCount + partialTick;

        // ===== Cube =====
        float cubeScale;
        float cubeSpin;

        if (t < chargeTicks) {
            // charging: grows with ease-in (slow start, fast end) and spins faster and faster
            float charge = t / chargeTicks;
            float shake = 1.0F + Mth.sin(time * 3.0F) * 0.08F * charge; // trembles more near full charge
            cubeScale = charge * charge * shake;
            cubeSpin = t * t / chargeTicks; // angle grows quadratically = accelerating rotation
        } else {
            // firing: cube collapses while the beam shoots out
            float shotT = t - chargeTicks;
            float collapse = Mth.clamp(1.0F - shotT / fireTicks, 0.0F, 1.0F);
            cubeScale = collapse * collapse; // fast at the start, smooth at the end
            cubeSpin = chargeTicks + 2.0F * shotT; // continues at the final charge speed
        }


        if (cubeScale > 0.01F) {
            poseStack.pushPose();
            poseStack.translate(0.0F, beamStart, 0.0F); // cube sits at the beam origin
            CeroHandler.renderGlowCube(poseStack, buffer, BeaconRenderer.BEAM_LOCATION,
                    cubeSpin, 10.0F,
                    colors, outerColors,
                    cubeSize * cubeScale, cubeGlowSize * cubeScale,
                    0.125F);
            poseStack.popPose();
        }

        // ===== Beam =====
        if (t >= chargeTicks) {
            float shotT = t - chargeTicks;

            // 1. extend: 0 -> 1 during fireTicks, with ease-out (fast start, smooth stop)
            float extend = Mth.clamp(shotT / fireTicks, 0.0F, 1.0F);
            extend = 1.0F - (1.0F - extend) * (1.0F - extend) * (1.0F - extend);

            // 3. shrink: stays 1, goes down to 0 during the last fadeTicks
            float shrink = Mth.clamp((shotTicks - shotT) / fadeTicks, 0.0F, 1.0F);

            // 2. pulse while holding
            float pulse = 1.0F + Mth.sin(time * 1.5F) * 0.05F;

            float radiusScale = shrink * pulse * Mth.lerp(extend, 0.6F, 1.0F);

            // skip when the beam is too thin (also avoids division by zero inside renderCero)
            if (radiusScale > 0.01F && extend > 0.0F) {
                CeroHandler.renderCero(poseStack, buffer, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0F,
                        entity.level().getGameTime(),
                        beamStart, maxLength * extend,      // animated length
                        colors, outerColors,
                        beamRadius * radiusScale,           // animated thickness
                        glowRadius * radiusScale,
                        0.125F, 0.0F);
            }
        }

        poseStack.popPose();
    }

}

    @Override
    public boolean shouldRender(IchigoVizard pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return pLivingEntity.getStage().equals(IchigoVizard.Stage.RAGE);
    }

    @Override
    public ResourceLocation getTextureLocation(IchigoVizard p_114482_) {
        return TEXTURE;
    }
}
