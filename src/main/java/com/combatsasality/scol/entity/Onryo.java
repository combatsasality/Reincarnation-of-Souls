package com.combatsasality.scol.entity;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.handlers.HelpHandler;
import com.combatsasality.scol.packets.client.PacketSetModelType;
import com.combatsasality.scol.registries.ScolEntities;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;

public class Onryo extends MonsterEntity {
//    @ObjectHolder(Main.MODID + ":onryo")
//    public static EntityType<Onryo> TYPE;
    public OnryoType onryoType = OnryoType.DEFAULT;
    public boolean canEscape = true;

    public Onryo(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.moveControl = new MoveHelperController(this);
    }
    public Onryo(World worldIn, double x, double y, double z, float yRot, float xRot) {
        this(ScolEntities.ONRYO, worldIn);
        this.setPos(x, y, z);
        this.yRot = yRot;
        this.xRot = xRot;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EscapeGoal(30, 8, 10));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, Onryo.class)).setAlertOthers(Onryo.class));
        this.targetSelector.addGoal(3, new NearestAttackOnryoTargetGoal(this, PlayerEntity.class, 10, false, true, new TargetPredicate()));
        this.targetSelector.addGoal(4, new NearestAttackOnryoTargetGoal(this, LivingEntity.class, 10, false, true, new TargetPredicate()));
        this.targetSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(6, new GetDownGoal());
        this.goalSelector.addGoal(7, new RandomFlyingGoal(10));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 1.0, 120, true));
    }

    public enum OnryoType {
        DEFAULT,
        FLY
    }
    public void setOnryoType(int onryoType) {
        this.onryoType = OnryoType.values()[onryoType];
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if ((this.getHealth() * 100 / this.getMaxHealth() <= 25) && this.onryoType == OnryoType.DEFAULT) {
                this.onryoType = OnryoType.FLY;
                Main.packetInstance.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new PacketSetModelType(this.getId(), this.onryoType.ordinal()));
                this.noPhysics = true;
                this.setNoGravity(true);
            }
        }
    }


    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MAX_HEALTH, 30.0d)
                .add(Attributes.ATTACK_DAMAGE, 10.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.5d)
                .add(Attributes.ARMOR, 10.0d)
                .add(Attributes.FLYING_SPEED, 3.0D);
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        Main.packetInstance.send(PacketDistributor.PLAYER.with(() -> player), new PacketSetModelType(this.getId(), this.onryoType.ordinal()));
        super.startSeenByPlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("onryoType", this.onryoType.ordinal());
        nbt.putBoolean("canEscape", this.canEscape);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.setOnryoType(nbt.getInt("onryoType"));
        this.canEscape = nbt.getBoolean("canEscape");
    }

    static class TargetPredicate implements Predicate<LivingEntity> {

        @Override
        public boolean test(LivingEntity livingEntity) {
            return livingEntity != null && livingEntity.getClass() != Onryo.class;
        }
    }
    class NearestAttackOnryoTargetGoal extends NearestAttackableTargetGoal {

        public NearestAttackOnryoTargetGoal(MobEntity mob, Class<?> targetType, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> predicate) {
            super(mob, targetType, randomInterval, mustSee, mustReach, predicate);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Onryo.this.onryoType == OnryoType.DEFAULT;
        }
    }

    class MoveHelperController extends MovementController {

        public MoveHelperController(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
            if (Onryo.this.onryoType == OnryoType.DEFAULT) {
                super.tick();
                return;
            }
            if (this.operation == Action.MOVE_TO) {

                if (Onryo.this.distanceToSqr(this.getWantedX(), this.getWantedY(), this.getWantedZ()) < 1.0D) {
                    this.operation = Action.WAIT;
                }
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double) 2.5000003E-7F) {
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    return;
                }
                if ((Math.abs(this.wantedX - Onryo.this.getX()) <= 1 &&
                        Math.abs(this.wantedY - Onryo.this.getY()) <= 1 &&
                        Math.abs(this.wantedZ - Onryo.this.getZ()) <= 1)) {
                    Vector3d vectorStop = Onryo.this.getDeltaMovement();
                    Onryo.this.setDeltaMovement(vectorStop.x, 0.0D, vectorStop.z);
                    this.operation = Action.WAIT;
                } else {
                    Vector3d vector3d = new Vector3d(this.wantedX - Onryo.this.getX(), this.wantedY - Onryo.this.getY(), this.wantedZ - Onryo.this.getZ());

                    vector3d = vector3d.normalize();
                    Onryo.this.setDeltaMovement(Onryo.this.getDeltaMovement().add(vector3d.scale(0.05D)));
                }

            }
            else if (this.operation == Action.WAIT) {
               Vector3d vectorStop = Onryo.this.getDeltaMovement();
               Onryo.this.setDeltaMovement(vectorStop.x, 0.0D, vectorStop.z);
            }
        }

    }

    abstract class MoveFlyingGoal extends Goal {
        protected BlockPos targetPos;
        protected int xz,y1,y2;
        protected boolean fromSolidBlock;
        protected int addFromSolidBlock = 0;

        public MoveFlyingGoal(int xz, int y1, int y2) {
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.xz = xz;
            this.y1 = y1;
            this.y2 = y2;
        }

        @Override
        public boolean canUse() {
            return Onryo.this.onryoType == OnryoType.FLY && !Onryo.this.getMoveControl().hasWanted() && this.createTargetPos();
        }

        @Override
        public void start() {
            Onryo.this.getMoveControl().setWantedPosition(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0D);
        }

        @Override
        public boolean canContinueToUse() {
            MovementController controller = Onryo.this.getMoveControl();
            return controller.hasWanted() && Onryo.this.distanceToSqr(controller.getWantedX(), controller.getWantedY(), controller.getWantedZ()) > 1.0D;
        }


        public boolean createTargetPos() {
            Random random = Onryo.this.getRandom();
            double x = Onryo.this.getX() + HelpHandler.getRandomDouble(random, -xz, xz);
            double z = Onryo.this.getZ() + HelpHandler.getRandomDouble(random, -xz, xz);
            if (fromSolidBlock) {
                BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, 256, z);
                while (blockpos$mutable.getY() > 0 && Onryo.this.level.isEmptyBlock(blockpos$mutable)) {
                    blockpos$mutable.move(Direction.DOWN);
                }
                blockpos$mutable.move(Direction.UP);
                blockpos$mutable.setY(blockpos$mutable.getY() + addFromSolidBlock + HelpHandler.getRandomInt(random, y1, y2));
                this.targetPos = blockpos$mutable.immutable();
            } else {
                this.targetPos = new BlockPos(x, Onryo.this.getY() + HelpHandler.getRandomInt(random, y1, y2), z);
            }
            return true;
        }
    }

    class EscapeGoal extends MoveFlyingGoal {
        public EscapeGoal(int xz, int y1, int y2) {
            super(xz, y1, y2);
            this.fromSolidBlock = false;
        }

        @Override
        public void start() {
            super.start();
            Onryo.this.setTarget(null);
            Onryo.this.canEscape = false;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && (Onryo.this.getHealth() * 100 / Onryo.this.getMaxHealth() <= 25) && Onryo.this.canEscape;
        }
    }
    class RandomFlyingGoal extends MoveFlyingGoal {
        protected int interval = 40;
        public RandomFlyingGoal(int xz) {
            super(xz, 0,0);
            this.fromSolidBlock = true;
            this.addFromSolidBlock = 8;
        }
        @Override
        public boolean canUse() {
            if (Onryo.this.getTarget() != null) {
                return false;
            }
            if (Onryo.this.getNoActionTime() >= 100) {
                return false;
            }
            if (Onryo.this.getRandom().nextInt(this.interval) != 0) {
                return false;
            }
            return super.canUse();
        }
        @Override
        public void tick() {
            super.tick();
            if (Onryo.this.getRandom().nextInt(80) == 0) {
                Onryo.this.heal(2f);
            }
        }
    }
    class RandomWalkingGoal extends net.minecraft.entity.ai.goal.RandomWalkingGoal {

        public RandomWalkingGoal(CreatureEntity mob, double speedModifier, int interval, boolean checkNoActionTime) {
            super(mob, speedModifier, interval, checkNoActionTime);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Onryo.this.onryoType == OnryoType.DEFAULT;
        }
    }
    class GetDownGoal extends Goal {
        protected BlockPos targetPos;

        public GetDownGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return Onryo.this.onryoType == OnryoType.FLY && !Onryo.this.getMoveControl().hasWanted() && Onryo.this.getMaxHealth() == Onryo.this.getHealth() && this.setPathFirstSolidBlock();
        }

        @Override
        public void start() {
            Onryo.this.getMoveControl().setWantedPosition(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0D);
        }

        @Override
        public void stop() {
            Onryo.this.setNoGravity(false);
            Onryo.this.noPhysics = false;
            Onryo.this.canEscape = true;
            Onryo.this.onryoType = OnryoType.DEFAULT;
            Main.packetInstance.send(PacketDistributor.TRACKING_ENTITY.with(() -> Onryo.this), new PacketSetModelType(Onryo.this.getId(), Onryo.this.onryoType.ordinal()));
        }

        @Override
        public boolean canContinueToUse() {
            MovementController controller = Onryo.this.getMoveControl();
            return controller.hasWanted() && Onryo.this.distanceToSqr(controller.getWantedX(), controller.getWantedY(), controller.getWantedZ()) > 1.0D;
        }

        public boolean setPathFirstSolidBlock() {
            double y = 256;
            if (Onryo.this.level.isEmptyBlock(Onryo.this.blockPosition())) {
                y = Onryo.this.getY();
            }
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(Onryo.this.getX(), y, Onryo.this.getZ());
            while (blockpos$mutable.getY() > 0 && Onryo.this.level.isEmptyBlock(blockpos$mutable)) {
                blockpos$mutable.move(0, -1, 0);
            }
            blockpos$mutable.move(0, 2, 0);
            this.targetPos = blockpos$mutable.immutable();
            return true;
        }

    }
}