package scol.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import scol.Main;

import java.util.List;

public class PowerWaveEntity extends DamagingProjectileEntity {



    @ObjectHolder(Main.modid + ":power_wave")
    public static EntityType<PowerWaveEntity> TYPE;
    public PowerWaveEntity(EntityType<? extends DamagingProjectileEntity> type, World world) {
        super(type, world);
    }

    public PowerWaveEntity(World worldIn, LivingEntity throwerIn) {
        super(PowerWaveEntity.TYPE, worldIn);
        this.setOwner(throwerIn);
        this.setPos(throwerIn.getX(), throwerIn.getY() + throwerIn.getEyeHeight(), throwerIn.getZ());
        this.xRot = throwerIn.xRot;
        this.yRot = throwerIn.yRot;
    }

    @Override
    public void tick() {
        super.baseTick();
        Vector3d vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d);
        this.setPos(this.getX() + vector3d.x, this.getY() + vector3d.y, this.getZ() + vector3d.z);

        if (!this.level.isClientSide() && this.getOwner() != null) {

            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, getBoundingBox());

            if (!entities.isEmpty()) {
                damageEntities(entities);
            }

            if (this.tickCount > 100) {
                this.remove();
            }
        }

        if (!this.level.isClientSide() && this.getOwner() == null) {
            this.remove();
        }
    }

    public void shootFromRotation() {
        float f = -MathHelper.sin(this.yRot * ((float)Math.PI / 180F)) * MathHelper.cos(this.xRot * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin(this.xRot * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(this.yRot * ((float)Math.PI / 180F)) * MathHelper.cos( this.xRot * ((float)Math.PI / 180F));
        this.setDeltaMovement(new Vector3d(f,f1,f2));
    }


    public void damageEntities(List<LivingEntity> listOfEntities) {
        PlayerEntity owner = (PlayerEntity) this.getOwner();
        for (LivingEntity entity : listOfEntities) {
            if (entity != owner && entity.isAttackable()) {
                entity.hurt(DamageSource.playerAttack(owner), (float) (owner.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 60.0F));
            }
        }
    }

    @Override
    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
