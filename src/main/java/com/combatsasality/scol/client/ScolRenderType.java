package com.combatsasality.scol.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class ScolRenderType {
    public static final RenderType GHOST = RenderType.create(
            "scol:ghost",
            DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 256,
            RenderType.State.builder()
                    .setTextureState(new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS, false, false))
                    .setAlphaState(new RenderState.AlphaState(0.5F) {
                        @Override
                        public void setupRenderState() {
                            RenderSystem.pushMatrix();
                            RenderSystem.color4f(1F, 1F, 1F, 1F);
                            GlStateManager._enableBlend();
                            GL14.glBlendColor(1.0F, 1.0F, 1.0F, 0.25F);
                            GlStateManager._blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA.value);
                        }

                        @Override
                        public void clearRenderState() {
                            GL14.glBlendColor(1.0F, 1.0F, 1.0F, 1.0F);
                            GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
                            RenderSystem.disableBlend();
                            RenderSystem.popMatrix();
                        }
                    })
                    .createCompositeState(false));
}
