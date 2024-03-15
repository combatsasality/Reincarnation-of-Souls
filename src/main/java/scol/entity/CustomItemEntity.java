package scol.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;
import scol.items.Zangetsu;
import scol.registries.ScolEntities;
import scol.registries.ScolItems;

import javax.annotation.Nullable;
import java.util.UUID;
/**
 * Все это включая CustomItemEntityRenderer спизжено у Aizistral-Studios\Enigmatic-Legacy
 */
public class CustomItemEntity extends Entity {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.defineId(CustomItemEntity.class, DataSerializers.ITEM_STACK);
    private int age;
    private int pickupDelay;
    private int health = 1000;
    private UUID thrower;
    private UUID owner;

//    @ObjectHolder(Main.MODID + ":custom_item_entity_ent")
//    public static EntityType<CustomItemEntity> TYPE;

    public float hoverStart = (float) (Math.random() * Math.PI * 2.0D);

    public CustomItemEntity(EntityType<CustomItemEntity> type, World world) {
        super(type, world);
    }

    public CustomItemEntity(World worldIn, double x, double y, double z) {
        this(ScolEntities.CUSTOM_ITEM_ENTITY_ENT, worldIn);
        this.setPos(x, y <= 0 ? 1 : y, z);
        this.yRot = this.random.nextFloat() * 360.0F;
        this.setInvulnerable(true);

        this.setNoGravity(true);
    }

    public CustomItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
    }

    public CustomItemEntity(World worldIn, BlockPos pos, ItemStack stack) {
        this(worldIn, pos.getX(), pos.getY(), pos.getZ());
        this.setItem(stack);
    }

    @OnlyIn(Dist.CLIENT)
    private CustomItemEntity(CustomItemEntity p_i231561_1_) {
        super(p_i231561_1_.getType(), p_i231561_1_.level);
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
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(CustomItemEntity.ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.remove();
        } else {
            super.tick();

            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
                --this.pickupDelay;
            }

            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            Vector3d vec3d = this.getDeltaMovement();

            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }
            if (Zangetsu.isDeathModel(this.getItem())) {
                this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            }

            if (this.level.isClientSide) {
                this.noPhysics = false;

                //this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + (this.getBbHeight() / 2), this.getZ(), ((Math.random() - 0.5) * 2.0), ((Math.random() - 0.5) * 2.0), ((Math.random() - 0.5) * 2.0));
            }
            if (!this.onGround || getHorizontalDistanceSqr(this.getDeltaMovement()) > (double)1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
                this.move(MoverType.SELF, this.getDeltaMovement());
                float f1 = 0.98F;
                if (this.onGround) {
                    f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getSlipperiness(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this) * 0.98F;
                }

                this.setDeltaMovement(this.getDeltaMovement().multiply((double)f1, 0.98D, (double)f1));
                if (this.onGround) {
                    Vector3d vector3d1 = this.getDeltaMovement();
                    if (vector3d1.y < 0.0D) {
                        this.setDeltaMovement(vector3d1.multiply(1.0D, -0.5D, 1.0D));
                    }
                }
            }
            ++this.age;

            if (!this.level.isClientSide) {
                double d0 = this.getDeltaMovement().subtract(vec3d).lengthSqr();
                if (d0 > 0.01D) {
                    this.hasImpulse = true;
                }
            }

            ItemStack item = this.getItem();

            if (item.isEmpty()) {
                this.remove();
            }

            // Portal Cooldown
            this.portalTime = Short.MAX_VALUE;
        }
    }

    @Override
    public Entity changeDimension(ServerWorld server, ITeleporter teleporter) {
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void remove() {
        super.remove();
    }


    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        compound.putShort("Health", (short) this.health);
        compound.putShort("Age", (short) this.age);
        compound.putShort("PickupDelay", (short) this.pickupDelay);
        if (this.getThrowerId() != null) {
            compound.putUUID("Thrower", this.getThrowerId());
        }

        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }

        if (!this.getItem().isEmpty()) {
            compound.put("Item", this.getItem().save(new CompoundNBT()));
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");
        if (compound.contains("PickupDelay")) {
            this.pickupDelay = compound.getShort("PickupDelay");
        }

        if (compound.contains("Owner")) {
            this.owner = compound.getUUID("Owner");
        }

        if (compound.contains("Thrower")) {
            this.thrower = compound.getUUID("Thrower");
        }

        CompoundNBT compoundnbt = compound.getCompound("Item");
        this.setItem(ItemStack.of(compoundnbt));
        if (this.getItem().isEmpty()) {
            this.remove();
        }

    }

    @Override
    public void playerTouch(PlayerEntity player) {
        if (!this.level.isClientSide) {
            if (this.pickupDelay > 0) return;
            ItemStack item = this.getItem();
            if (item.getItem().equals(ScolItems.ZANGETSU)) {
                if (Zangetsu.getOwner(item).equals(player.getGameProfile().getName())) {
                    if (player.inventory.getFreeSlot() != -1) {
                        Zangetsu.setDeathModel(item, false);
                        Zangetsu.setDisableGravity(item, false);
                        player.inventory.add(item);
                        player.take(this, 1);
                        item.setCount(0);
                        this.remove();
                        player.awardStat(Stats.ITEM_PICKED_UP.get(item.getItem()), 1);
                        return;
                    }
                }
                if (Zangetsu.getOwner(item).isEmpty()) {
                    item.setCount(0);
                    this.remove();
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
    public ITextComponent getName() {
        ITextComponent itextcomponent = this.getCustomName();
        return itextcomponent != null ? itextcomponent : new TranslationTextComponent(this.getItem().getDescriptionId());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public ItemStack getItem() {
        return this.getEntityData().get(CustomItemEntity.ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getEntityData().set(CustomItemEntity.ITEM, stack);
    }

    @Nullable
    public UUID getOwnerId() {
        return this.owner;
    }

    @Nullable
    public UUID getThrowerId() {
        return this.thrower;
    }

    @OnlyIn(Dist.CLIENT)
    public int getAge() {
        return this.age;
    }

    public void setInfinitePickupDelay() {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int ticks) {
        this.pickupDelay = ticks;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
