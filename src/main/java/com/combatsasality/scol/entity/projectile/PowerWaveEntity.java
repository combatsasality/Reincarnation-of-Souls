package com.combatsasality.scol.entity.projectile;

import com.combatsasality.scol.registries.ScolEntities;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class PowerWaveEntity extends Projectile {
    public PowerWaveEntity(EntityType<PowerWaveEntity> type, Level level) {
        super(type, level);
    }

    public PowerWaveEntity(Level level, LivingEntity throwerIn) {
        super(ScolEntities.POWER_WAVE_ENTITY, level);
        this.setOwner(throwerIn);
        this.setPos(throwerIn.getX(), throwerIn.getEyeY(), throwerIn.getZ());
        this.setXRot(throwerIn.getXRot());
        this.setYRot(throwerIn.getYRot());
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.baseTick();
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d);
        this.setPos(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
        if (!this.level().isClientSide && this.getOwner() != null) {
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, getBoundingBox());
            if (!entities.isEmpty()) {
                damageEntities(entities);
            }

            if (this.tickCount > 100) {
                this.discard();
            }
        }
        if (!this.level().isClientSide && this.getOwner() == null) {
            this.discard();
        }
    }
    public void damageEntities(List<LivingEntity> listOfEntities) {
        Player owner = (Player) this.getOwner();
        for (LivingEntity entity : listOfEntities) {
            if (entity != owner && entity.isAttackable()) {
                entity.hurt(entity.damageSources().playerAttack(owner), (float) (owner.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 60.0F));
            }
        }
    }

    public void shootFromRotation() {
        float f = -Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * Mth.cos(this.getXRot() * ((float)Math.PI / 180F));
        float f1 = -Mth.sin(this.getXRot() * ((float)Math.PI / 180F));
        float f2 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * Mth.cos( this.getXRot() * ((float)Math.PI / 180F));
        this.setDeltaMovement(new Vec3(f,f1,f2));
    }

    @Override
    public boolean hurt(DamageSource p_19946_, float p_19947_) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
