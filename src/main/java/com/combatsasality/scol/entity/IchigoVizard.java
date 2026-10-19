package com.combatsasality.scol.entity;

import com.combatsasality.scol.items.Zangetsu;
import com.combatsasality.scol.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class IchigoVizard extends Monster {
    private int cooldownSonido = 0;
    private static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CURRENT_TARGET =
            SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CERO = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    private boolean rageTriggered = false;
    // tick when the current Cero phase started, updated on both sides in onSyncedDataUpdated
    private int ceroStartTick = 0;
    private int ceroStageTick = 0;

    private final List<Direction> beamSides = new ArrayList<>();
    @Nullable
    private Direction lastBeamSide;


    public IchigoVizard(EntityType<IchigoVizard> type, Level level) {
        super(type, level);
        ItemStack zangetsu = new ItemStack(ScolItems.ZANGETSU);
        Zangetsu.setBankai(zangetsu, true);
        this.setItemInHand(InteractionHand.MAIN_HAND, zangetsu);
    }

    public IchigoVizard(Level level, double x, double y, double z) {
        this(ScolEntities.ICHIGO_VAZARD, level);
        this.setPos(x, y, z);
        this.setYRot(this.random.nextFloat() * 360.0F);
    }

    public IchigoVizard(Level level, BlockPos pos) {
        this(level, pos.getX(), pos.getY(), pos.getZ());
    }

    public IchigoVizard(Level level, Vec3 vec3) {
        this(level, vec3.x, vec3.y, vec3.z);
    }

    private final ServerBossEvent bossEvent = (ServerBossEvent)
            new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)
                    .setDarkenScreen(true);

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100.0D);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !getStage().equals(Stage.RAGE);
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !getStage().equals(Stage.RAGE);
            }
        });
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !getStage().equals(Stage.RAGE);
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !getStage().equals(Stage.RAGE);
            }
        });
        this.targetSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !getStage().equals(Stage.RAGE);
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !getStage().equals(Stage.RAGE);
            }
        });
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setStage(Stage.byId(pCompound.getInt("scol.stage")));
        setCero(Cero.byId(pCompound.getInt("scol.cero")));
        this.rageTriggered = pCompound.getBoolean("scol.rageTriggered");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("scol.stage", getStage().ordinal());
        pCompound.putInt("scol.cero", getCero().ordinal());
        pCompound.putBoolean("scol.rageTriggered", this.rageTriggered);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STAGE, Stage.DEFAULT.ordinal());
        this.entityData.define(CURRENT_TARGET, -1);
        this.entityData.define(CERO, Cero.PAUSE.ordinal());
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (CERO.equals(key)) {
            this.ceroStartTick = this.tickCount; // restart the phase timer whenever the Cero state changes
        }
    }

    /** Ticks since the current Cero phase started (used by the renderer for the attack animation). */
    public float getCeroTicks(float partialTick) {
        return this.tickCount - this.ceroStartTick + partialTick;
    }

    @Override
    public int getExperienceReward() {
        return 100;
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        }
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        if (this.canAttack((LivingEntity) entity)) {
            entity.hurt(
                    this.damageSources().source(ScolDamageTypes.ICHIGO, this),
                    (this.random.nextInt((8 - 5 + 1) + 5) * 2));
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        if (getStage().equals(Stage.RAGE) && !source.is(DamageTypes.GENERIC_KILL)) {
            return false;
        }

        if (this.random.nextFloat() > 0.66) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                float returnDamage = (float) (damage * 0.75);
                DamageSource returnSource = new DamageSource(source.typeHolder(), source.getEntity(), this, null);
                attacker.hurt(returnSource, returnDamage);
            }
        }

        if (source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.FALLING_BLOCK)) {
            return false;
        }

        return super.hurt(source, damage);
    }

    @Override
    public void awardKillScore(@NotNull Entity ent, int pScoreValue, @NotNull DamageSource source) {
        this.heal(this.getMaxHealth());
        super.awardKillScore(ent, pScoreValue, source);
    }

    @Override
    protected void customServerAiStep() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
    }

    /** Instantly looks at the given point. */
    public void snapLookAt(Vec3 point) {
        double dx = point.x - this.getX();
        double dz = point.z - this.getZ();
        double dy = point.y - this.getEyeY();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) -(Mth.atan2(dy, horizontal) * Mth.RAD_TO_DEG);

        // current rotation
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yHeadRot = yaw;
        this.yBodyRot = yaw;

        // previous-tick rotation: set the same, so there is no smooth turn between ticks
        this.yRotO = yaw;
        this.xRotO = pitch;
        this.yHeadRotO = yaw;
        this.yBodyRotO = yaw;
    }

    /** Instantly looks at the center of the block. */
    public void snapLookAt(BlockPos pos) {
        this.snapLookAt(Vec3.atCenterOf(pos));
    }

    /** Instantly looks at the entity's eyes. */
    public void snapLookAt(Entity target) {
        this.snapLookAt(target.getEyePosition());
    }

    @Override
    public void tick() {
        super.tick();

        // Placeholder DELETE!
//        List<Player> playa = this.getPlayersAround(30);
//
//        if (!playa.isEmpty()) {
//            this.setTarget(playa.get(0));
//        }
        if (this.isAlive()) {
            // Triggering rage
            if (this.getHealth() / this.getMaxHealth() < 0.25F && !this.rageTriggered) {
                setStage(Stage.RAGE);
                this.rageTriggered = true;
            } else if (getStage().equals(Stage.RAGE)) {
                if (getSyncedTarget() == null || !getSyncedTarget().isAlive()) {
                    List<Player> players = this.getPlayersAround(30);
                    if (!players.isEmpty()) {
                        setSyncedTarget(players.get(0));
                    }
                } else {
                    snapLookAt(getSyncedTarget());

                    if (!this.level().isClientSide) {
                        if (this.ceroStageTick == 0) {
                            BlockPos pos = getRandomPositionForBeam();
                            setPos(pos.getX(), pos.getY(), pos.getZ());
                            setCero(Cero.CHARGING);
                        } else if (this.ceroStageTick == 120) {
                            setCero(Cero.SHOT);
                        } else if (this.ceroStageTick == 180) {
                            setCero(Cero.PAUSE);
                        }

                        this.ceroStageTick++;
                        if (this.ceroStageTick >= 190) { // 80 ticks of pause, then loop
                            this.ceroStageTick = 0;
                        }
                    }
                }
            }


        }




//        if (this.isAlive() && !this.level().isClientSide) {
//            if (this.cooldownSonido != 0) {
//                this.cooldownSonido--;
//            } else if (this.getTarget() != null) {
//                LivingEntity target = this.getTarget();
//                if (this.canAttack(target)) {
//                    Path path = this.getNavigation().createPath(target, 1);
//                    if (this.getVisibilityPercent(target) < 0.1
//                            || (path == null || !path.canReach())
//                            || Math.abs(Math.abs(target.getY()) - Math.abs(this.getY())) > 1.5) {
//                        this.setPos(target.getX(), target.getY(), target.getZ());
//                        this.cooldownSonido = 200;
//                        this.doHurtTarget(target);
//                        this.playSound(ScolSounds.SONIDO, 50f, 1f);
//                    }
//                }
//            }
//            List<Player> players = this.getPlayersAround(30);
//
//            if (!players.isEmpty()) {
//                for (Player player : players) {
//                    player.getAbilities().flying &= player.isCreative();
//                    player.onUpdateAbilities();
//                }
//            }
//        }
    }

    public List<Player> getPlayersAround(double range) {
        AABB box = new AABB(
                this.getX() - range,
                this.getY() - range,
                this.getZ() - range,
                this.getX() + range,
                this.getY() + range,
                this.getZ() + range);
        return this.level()
                .getEntitiesOfClass(
                        Player.class, box, player -> !player.isSpectator() && !(player instanceof FakePlayer));
    }

    private Direction nextBeamSide() {
        if (this.beamSides.isEmpty()) {
            this.beamSides.addAll(Direction.Plane.HORIZONTAL.stream().toList());

            // shuffle (Fisher-Yates)
            for (int i = this.beamSides.size() - 1; i > 0; i--) {
                int j = this.random.nextInt(i + 1);
                Collections.swap(this.beamSides, i, j);
            }

            // avoid the same side twice in a row across two rounds
            if (this.beamSides.get(0) == this.lastBeamSide) {
                Collections.swap(this.beamSides, 0, this.beamSides.size() - 1);
            }
        }

        this.lastBeamSide = this.beamSides.remove(0);
        return this.lastBeamSide;
    }

    @Nullable
    public BlockPos getRandomPositionForBeam() {
        LivingEntity target = this.getSyncedTarget();
        if (target == null) {
            return null;
        }

        BlockPos targetPos = target.blockPosition();

        // random horizontal side: north, south, east or west
        Direction side = this.nextBeamSide();
        int distance = Mth.nextInt(this.random, 10, 20);   // how far from the target along that side
        int sideways = Mth.nextInt(this.random, -10, 10);  // random shift along the other horizontal axis

        // same Y as the target
        return targetPos
                .relative(side, distance)
                .relative(side.getClockWise(), sideways);
    }


    @Override
    protected void dropCustomDeathLoot(DamageSource source, int p_21386_, boolean p_21387_) {
        if (source.getEntity() instanceof ServerPlayer player) {
            player.getCapability(ScolCapabilities.SCOL_CAPABILITY).ifPresent(capa -> {
                if (!capa.isHasZangetsu()) {
                    ItemStack stack = new ItemStack(ScolItems.ZANGETSU);
                    Zangetsu.setDeathModel(stack, true);
                    Zangetsu.setDisableGravity(stack, true);
                    stack.getOrCreateTag()
                            .putString("scol.Owner", player.getGameProfile().getName());
                    CustomItemEntity dropped =
                            new CustomItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack);
                    dropped.setPickupDelay(40);
                    if (this.level().addFreshEntity(dropped)) {
                        capa.setHasZangetsu(true);
                    }
                }
            });
        }
    }

    public Stage getStage() {
        return Stage.byId(this.entityData.get(STAGE));
    }

    public void setStage(Stage state) {
        this.entityData.set(STAGE, state.ordinal());
    }

    public Cero getCero() {
        return Cero.byId(this.entityData.get(CERO));
    }

    public void setCero(Cero state) {
        this.entityData.set(CERO, state.ordinal());
    }

    // server
    public void setSyncedTarget(@Nullable LivingEntity target) {
        this.entityData.set(CURRENT_TARGET, target == null ? -1 : target.getId());
    }

    // client and server
    @Nullable
    public LivingEntity getSyncedTarget() {
        int id = this.entityData.get(CURRENT_TARGET);
        if (id == -1) return null;
        return this.level().getEntity(id) instanceof LivingEntity living ? living : null;
    }

    public enum Stage {
        DEFAULT,
        RAGE;

        private static final Stage[] VALUES = values(); // values() creates a new array on every call, so cache it

        public static Stage byId(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : DEFAULT;
        }
    }

    public enum Cero {
        PAUSE,
        CHARGING,
        SHOT;

        private static final Cero[] VALUES = values(); // values() creates a new array on every call, so cache it

        public static Cero byId(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : PAUSE;
        }
    }

}
