package com.combatsasality.scol.client.renderer;

import com.combatsasality.scol.entity.CustomItemEntity;
import com.combatsasality.scol.items.Zangetsu;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class CustomItemRenderer extends EntityRenderer<CustomItemEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public CustomItemRenderer(EntityRendererProvider.Context renderManagerIn, ItemRenderer itemRendererIn) {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.1F;
    }

    protected int getModelCount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    @Override
    public void render(CustomItemEntity entityIn, float entityYaw, float partialTicks, PoseStack PoseStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (!Minecraft.getInstance().player.isAlive() && Math.sqrt(entityIn.distanceToSqr(Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getEyeY(), Minecraft.getInstance().player.getZ())) <= 1.0)
            return;
        PoseStackIn.pushPose();
        ItemStack itemstack = entityIn.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
        this.random.setSeed(i);
        BakedModel ibakedmodel = this.itemRenderer.getModel(itemstack, entityIn.level(), null, entityIn.getId());
        if (Zangetsu.isDisableGravity(itemstack)) {
            PoseStackIn.translate(0.0D, 0.25F, 0.0D);
            PoseStackIn.mulPose(Axis.YP.rotation(0));
            this.itemRenderer.render(itemstack, ItemDisplayContext.GROUND, false, PoseStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
            // TODO: add so he can look in different directions
        } else {
            boolean flag = ibakedmodel.isGui3d();
            int j = this.getModelCount(itemstack);
            float f = 0.25F;
            float f1 = Mth.sin((entityIn.getAge() + partialTicks) / 10.0F + entityIn.hoverStart) * 0.1F + 0.1F;
            float f2 = this.shouldBob() ? ibakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y() : 0;
            PoseStackIn.translate(0.0D, f1 + 0.25F * f2, 0.0D);
            float f3 = entityIn.getItemHover(partialTicks);
            PoseStackIn.mulPose(Axis.YP.rotation(f3));
            if (!flag) {
                float f7 = -0.0F * (j - 1) * 0.5F;
                float f8 = -0.0F * (j - 1) * 0.5F;
                float f9 = -0.09375F * (j - 1) * 0.5F;
                PoseStackIn.translate(f7, f8, f9);
            }

            for (int k = 0; k < j; ++k) {
                PoseStackIn.pushPose();
                if (k > 0) {
                    if (flag) {
                        float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        PoseStackIn.translate(this.shouldSpreadItems() ? f11 : 0, this.shouldSpreadItems() ? f13 : 0, this.shouldSpreadItems() ? f10 : 0);
                    } else {
                        float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        PoseStackIn.translate(this.shouldSpreadItems() ? f12 : 0, this.shouldSpreadItems() ? f14 : 0, 0.0D);
                    }
                }

                this.itemRenderer.render(itemstack, ItemDisplayContext.GROUND, false, PoseStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
                PoseStackIn.popPose();
                if (!flag) {
                    PoseStackIn.translate(0.0, 0.0, 0.09375F);
                }
            }
        }
        PoseStackIn.popPose();

        super.render(entityIn, entityYaw, partialTicks, PoseStackIn, bufferIn, packedLightIn);
    }


    /**
     * Returns the location of an entity's texture.
     */

    @Override
    public ResourceLocation getTextureLocation(CustomItemEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    /*==================================== FORGE START ===========================================*/

    /**
     * @return If items should spread out when rendered in 3D
     */
    public boolean shouldSpreadItems() {
        return true;
    }

    /**
     * @return If items should have a bob effect
     */
    public boolean shouldBob() {
        return true;
    }
    /*==================================== FORGE END =============================================*/
}
