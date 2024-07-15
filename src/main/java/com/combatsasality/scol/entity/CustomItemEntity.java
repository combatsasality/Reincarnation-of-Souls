package com.combatsasality.scol.entity;

import com.combatsasality.scol.items.Zangetsu;
import com.combatsasality.scol.registries.ScolEntities;
import com.combatsasality.scol.registries.ScolItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Author Aizistral-Studios\Enigmatic-Legacy
 */
public class CustomItemEntity extends Entity {
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(CustomItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private int age;
    private int pickupDelay;
    private int health = 5;
    private UUID thrower;
    private UUID owner;
    private Vec3 position;

    public float hoverStart = (float) (Math.random() * Math.PI * 2.0D);

    public CustomItemEntity(EntityType<CustomItemEntity> type, Level level) {
        super(type, level);
    }

    public CustomItemEntity(Level level, double x, double y, double z) {
        this(ScolEntities.CUSTOM_ITEM_ENTITY, level);
        y = y <= level.getMinBuildHeight() ? level.getMinBuildHeight() + 8 : y;
        this.setPos(x, y, z);
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.setInvulnerable(true);
        this.setNoGravity(true);
    }

    public CustomItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        this(level, x, y, z);
        this.setItem(stack);
    }

    @OnlyIn(Dist.CLIENT)
    private CustomItemEntity(CustomItemEntity p_i231561_1_) {
        super(p_i231561_1_.getType(), p_i231561_1_.level());
        this.setItem(p_i231561_1_.getItem().copy());
        this.copyPosition(p_i231561_1_);
        this.age = p_i231561_1_.age;
        this.hoverStart = p_i231561_1_.hoverStart;
    }

    @OnlyIn(Dist.CLIENT)
    public CustomItemEntity copy() {
        return new CustomItemEntity(this);
    }

    @Override
    public boolean dampensVibrations() {
        return this.getItem().is(ItemTags.DAMPENS_VIBRATIONS);
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(CustomItemEntity.ITEM, ItemStack.EMPTY);
    }

    @Override
    public int getDimensionChangingDelay() {
        return Short.MAX_VALUE;
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel level, ITeleporter teleporter) {
        return null;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.discard();
            return;
        }
        if (!this.level().isClientSide && this.position != null) {
            if (!this.position.equals(this.position)) {
                this.teleportTo(this.position.x, this.position.y, this.position.z);
            }
        }
        super.tick();

        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
        }
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        Vec3 vec3d = this.getDeltaMovement();

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        if (Zangetsu.isDeathModel(this.getItem())) {
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        }
        if (this.level().isClientSide) {
            this.noPhysics = false;
        } else {
            this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7D));
            if (this.noPhysics) {
                this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
            }
        }
        if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > (double)1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f1 = 0.98F;
            if (this.onGround()) {
                BlockPos groundPos = getBlockPosBelowThatAffectsMyMovement();
                f1 = this.level().getBlockState(groundPos).getFriction(level(), groundPos, this) * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f1, 0.98D, (double)f1));
            if (this.onGround()) {
                Vec3 vec31 = this.getDeltaMovement();
                if (vec31.y < 0.0D) {
                    this.setDeltaMovement(vec31.multiply(1.0D, -0.5D, 1.0D));
                }
            }
        }


        ++this.age;

        if (!this.level().isClientSide) {
            double d0 = this.getDeltaMovement().subtract(vec3d).lengthSqr();
            if (d0 > 0.0D) {
                this.hasImpulse = true;
            }
        }
        ItemStack item = this.getItem();
        if (item.isEmpty()) {
            this.discard();
        }
        this.setPortalCooldown();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || !this.isAlive()) return false;

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason == RemovalReason.DISCARDED || reason == RemovalReason.KILLED) {
            // TODO: add logic set isHaveZangetsu to false if item is zangetsu
        }
        super.remove(reason);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("Health", (short) this.health);
        tag.putShort("Age", (short) this.age);
        tag.putShort("PickupDelay", (short) this.pickupDelay);
        if (this.getThrowerId() != null) {
            tag.putUUID("Thrower", this.getThrowerId());
        }
        if (this.getOwnerId() != null) {
            tag.putUUID("Owner", this.getOwnerId());
        }
        if (!this.getItem().isEmpty()) {
            tag.put("Item", this.getItem().save(new CompoundTag()));
        }
        if (this.position != null) {
            tag.putDouble("BoundX", this.position.x);
            tag.putDouble("BoundY", this.position.y);
            tag.putDouble("BoundZ", this.position.z);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.health = tag.getShort("Health");
        this.age = tag.getShort("Age");
        if (tag.contains("PickupDelay")) {
            this.pickupDelay = tag.getShort("PickupDelay");
        }
        if (tag.contains("Owner")) {
            this.owner = tag.getUUID("Owner");
        }
        if (tag.contains("Thrower")) {
            this.thrower = tag.getUUID("Thrower");
        }
        if (tag.contains("BoundX") && tag.contains("BoundY") && tag.contains("BoundZ")) {
            double x = tag.getDouble("BoundX");
            double y = tag.getDouble("BoundY");
            double z = tag.getDouble("BoundZ");
            this.position = new Vec3(x, y, z);
        }

        CompoundTag itemTag = tag.getCompound("Item");
        this.setItem(ItemStack.of(itemTag));
        if (this.getItem().isEmpty()) {
            this.discard();
        }
    }

    public void setItem(ItemStack stack) {
        this.getEntityData().set(CustomItemEntity.ITEM, stack);
    }
    public ItemStack getItem() {
        return this.getEntityData().get(CustomItemEntity.ITEM);
    }
    @Nullable
    public UUID getThrowerId() {
        return this.thrower;
    }
    public void setThrowerId(@Nullable UUID throwerId) {
        this.thrower = throwerId;
    }

    @OnlyIn(Dist.CLIENT)
    public int getAge() {
        return this.age;
    }

    @Nullable
    public UUID getOwnerId() {
        return this.owner;
    }

    public void setOwnerId(@Nullable UUID ownerId) {
        this.owner = ownerId;
    }
    public void setNoPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setInfinitePickupDelay() {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int ticks) {
        this.pickupDelay = ticks;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void makeFakeItem() {
        this.setInfinitePickupDelay();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void playerTouch(Player player) {
        if (this.level().isClientSide) return;
        if (this.pickupDelay > 0) return;
        ItemStack item = this.getItem();

        if (item.getItem().equals(ScolItems.ZANGETSU)) {
            if (Zangetsu.getOwner(item).equals(player.getGameProfile().getName())) {
                if (player.getInventory().getFreeSlot() != -1) {
                    Zangetsu.setDeathModel(item, false);
                    Zangetsu.setDisableGravity(item, false);
                    player.getInventory().add(item);
                    player.take(this,1);
                    item.setCount(0);
                    this.discard();
                    player.awardStat(Stats.ITEM_PICKED_UP.get(item.getItem()), 1);
                    return;
                }
                if (Zangetsu.getOwner(item).isEmpty()) {
                    item.setCount(0);
                    this.discard();
                    return;
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getItemHover(float partialTicks) {
        return (this.getAge() + partialTicks) / 20.0F + this.hoverStart;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }
    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return false;
    }

    @Override
    public Component getName() {
        Component itextcomponent = this.getCustomName();
        return itextcomponent != null ? itextcomponent : Component.translatable(this.getItem().getDescriptionId());
    }
}
