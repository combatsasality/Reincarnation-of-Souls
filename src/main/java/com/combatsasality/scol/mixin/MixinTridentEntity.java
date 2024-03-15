package com.combatsasality.scol.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class MixinTridentEntity {
    @Shadow private ItemStack tridentItem;
    @Inject(method = "onHitEntity", at = @At("HEAD"))
    public void onHitEntity(EntityRayTraceResult rayTraceResult, CallbackInfo info) {
        Entity entity = rayTraceResult.getEntity();
        TridentEntity tridentEntity = (TridentEntity) (Object) this;
        Entity entity1 = tridentEntity.getOwner();
        if (tridentEntity.level instanceof ServerWorld && !tridentEntity.level.isThundering() && this.tridentItem.getOrCreateTag().getBoolean("scol.IsImproved") && EnchantmentHelper.hasChanneling(this.tridentItem)) {
            BlockPos blockpos = entity.blockPosition();
            if (tridentEntity.level.canSeeSky(blockpos)) {
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(tridentEntity.level);
                lightningboltentity.moveTo(Vector3d.atBottomCenterOf(blockpos));
                lightningboltentity.setCause(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity1 : null);
                tridentEntity.level.addFreshEntity(lightningboltentity);
                tridentEntity.playSound(SoundEvents.TRIDENT_THUNDER, 5.0F, 1.0F);
            }
        }
    }
}
