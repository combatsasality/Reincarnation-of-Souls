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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class IchigoVizardRenderer extends MobRenderer<IchigoVizard, IchigoVizardModel<IchigoVizard>> {
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(Main.MODID, "textures/entity/ichigo_vizard.png");
    public static final float[] colors = {0.0F, 0.0F, 0.0F};
    public static final float[] outerColors = {0.85F, 0.0F, 0.0F};
    // geometry and timing come from the entity so the visuals match the server-side hit test
    public static final float beamStart = IchigoVizard.CERO_START;
    public static final float beamForward = IchigoVizard.CERO_FORWARD;
    public static final float maxLength = 1024.0F;
    public static final float beamRadius = IchigoVizard.CERO_RADIUS;
    public static final float glowRadius = 0.55F;
    public static final float cubeSize = 0.4F;
    public static final float cubeGlowSize = 0.45F;
    public static final float chargeTicks = IchigoVizard.CERO_CHARGE_TICKS; // cube grows
    public static final float lockTicks = IchigoVizard.CERO_LOCK_TICKS;     // cube at full size, aim frozen
    public static final float fireTicks = IchigoVizard.CERO_FIRE_TICKS;     // beam shoots out
    public static final float holdTicks = IchigoVizard.CERO_HOLD_TICKS;     // beam holds
    public static final float fadeTicks = IchigoVizard.CERO_FADE_TICKS;     // beam and cube fade


    public IchigoVizardRenderer(EntityRendererProvider.Context context) {
        super(context, new IchigoVizardModel<>(IchigoVizardModel.createModel().bakeRoot()), 1.0f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

@Override
public void render(IchigoVizard entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

    LivingEntity target = entity.getSyncedTarget();

    if (entity.getStage().equals(IchigoVizard.Stage.RAGE) && target != null) {
        // ===== Timeline (in ticks), driven by the entity's synced Cero state =====
        float shotTicks = fireTicks + holdTicks + fadeTicks;
        float shotStart = chargeTicks + lockTicks;
        float t = getCeroTime(entity, partialTick);

        if (t < 0.0F || t >= shotStart + shotTicks) {
            return; // paused, or the shot has fully faded out
        }

        // ===== Aim: the synced beam angles, the same ones the server tests hits against =====
        float bodyYaw = entity.getCeroYaw(partialTick);
        float pitch = entity.getCeroPitch(partialTick);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.translate(0.0F, 0.0F, -beamForward);
        poseStack.rotateAround(Axis.XP.rotationDegrees(-90.0F + pitch), 0.0F, beamStart, 0.0F);

        float time = entity.tickCount + partialTick;

        // ===== Cube: grows while charging, holds while locked, collapses during the first fireTicks of the shot =====
        if (t < shotStart + fireTicks) {
            float cubeScale;
            float cubeSpin;

            if (t < chargeTicks) {
                // charging: grows with ease-in (slow start, fast end) and spins faster and faster
                float charge = t / chargeTicks;
                float shake = 1.0F + Mth.sin(time * 3.0F) * 0.08F * charge; // trembles more near full charge
                cubeScale = charge * charge * shake;
                cubeSpin = t * t / chargeTicks; // angle grows quadratically = accelerating rotation
            } else if (t < shotStart) {
                // locked: full size, trembling harder = the telegraph for the player to step aside
                cubeScale = 1.0F + Mth.sin(time * 3.0F) * 0.12F;
                cubeSpin = chargeTicks + 2.0F * (t - chargeTicks); // continues at the final charge speed
            } else {
                // firing: cube collapses while the beam shoots out
                float shotT = t - shotStart;
                float collapse = Mth.clamp(1.0F - shotT / fireTicks, 0.0F, 1.0F);
                cubeScale = collapse * collapse; // fast at the start, smooth at the end
                cubeSpin = chargeTicks + 2.0F * (t - chargeTicks);
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
        }


        // ===== Beam: only during the shot =====
        if (t >= shotStart) {
            float shotT = t - shotStart;

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

    /**
     * Maps the entity's Cero state onto the attack timeline (in ticks):
     * CHARGING -> [0, chargeTicks],
     * LOCKED -> [chargeTicks, chargeTicks + lockTicks],
     * SHOT -> [chargeTicks + lockTicks, + fireTicks + holdTicks + fadeTicks].
     * Phases that the server holds longer than expected stay at their last frame.
     * Returns -1 while the Cero is paused, i.e. nothing to draw.
     */
    private static float getCeroTime(IchigoVizard entity, float partialTick) {
        float phaseTicks = entity.getCeroTicks(partialTick);
        return switch (entity.getCero()) {
            case CHARGING -> Math.min(phaseTicks, chargeTicks);
            case LOCKED -> chargeTicks + Math.min(phaseTicks, lockTicks);
            case SHOT -> chargeTicks + lockTicks + phaseTicks;
            default -> -1.0F;
        };
    }

    @Override
    public boolean shouldRender(IchigoVizard pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return pLivingEntity.getStage().equals(IchigoVizard.Stage.RAGE) || super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ);
    }

    @Override
    public ResourceLocation getTextureLocation(IchigoVizard p_114482_) {
        return TEXTURE;
    }
}
