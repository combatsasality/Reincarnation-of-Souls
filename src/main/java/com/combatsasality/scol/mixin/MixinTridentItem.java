package com.combatsasality.scol.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class MixinTridentItem {

    @Inject(method= "releaseUsing", at = @At("HEAD"))
    public void onReleaseUsing(ItemStack stack, World world, LivingEntity living, int useduration, CallbackInfo info) {
        if (living instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) living;
            int i = 72000 - useduration;
            if (i >= 10) {
                int riptideEnchant = EnchantmentHelper.getRiptide(stack);
                if (riptideEnchant > 0 && stack.getOrCreateTag().getBoolean("scol.IsImproved")) {
                    float f7 = playerEntity.yRot;
                    float f = playerEntity.xRot;
                    float f1 = -MathHelper.sin(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                    float f2 = -MathHelper.sin(f * ((float)Math.PI / 180F));
                    float f3 = MathHelper.cos(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                    float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                    float f5 = 3.0F * ((1.0F + (float)riptideEnchant) / 4.0F);
                    f1 = f1 * (f5 / f4);
                    f2 = f2 * (f5 / f4);
                    f3 = f3 * (f5 / f4);
                    playerEntity.push((double)f1, (double)f2, (double)f3);
                    playerEntity.startAutoSpinAttack(20);
                    if (playerEntity.isOnGround()) {
                        float f6 = 1.1999999F;
                        playerEntity.move(MoverType.SELF, new Vector3d(0.0D, (double)1.1999999F, 0.0D));
                    }

                    SoundEvent soundEvent;
                    if (riptideEnchant >= 3) {
                        soundEvent = SoundEvents.TRIDENT_RIPTIDE_3;
                    } else if (riptideEnchant == 2) {
                        soundEvent = SoundEvents.TRIDENT_RIPTIDE_2;
                    } else {
                        soundEvent = SoundEvents.TRIDENT_RIPTIDE_1;
                    }

                    world.playSound(null, playerEntity, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }
    @Inject(method = "use", at = @At("RETURN"), cancellable = true)
    public void onUse(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (EnchantmentHelper.getRiptide(stack) > 0 && stack.getOrCreateTag().getBoolean("scol.IsImproved")) {
            player.startUsingItem(hand);
            cir.setReturnValue(ActionResult.consume(stack));
        }
    }
}
