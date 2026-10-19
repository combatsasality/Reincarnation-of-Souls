package com.combatsasality.scol.entity;

import com.combatsasality.scol.items.Zangetsu;
import com.combatsasality.scol.registries.*;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class IchigoVizard extends Monster {
    // ===== Cero timeline (ticks), shared by the server state machine and the renderer =====
    public static final int CERO_CHARGE_TICKS = 40;  // cube grows, boss tracks the target
    public static final int CERO_FIRE_TICKS = 20;    // beam shoots out
    public static final int CERO_HOLD_TICKS = 30;    // beam holds
    public static final int CERO_FADE_TICKS = 10;    // beam and cube fade
    public static final int CERO_PAUSE_TICKS = 5;   // nothing happens before the next charge
    public static final int CERO_SHOT_TICKS = CERO_FIRE_TICKS + CERO_HOLD_TICKS + CERO_FADE_TICKS;

    // The dodge window: the aim is frozen for this long before the shot. While charging, the aim LEADS the
    // target (velocity + acceleration), so a player who keeps running gets hit where they will be. A running
    // player could still dodge by simply STOPPING (braking takes 1-2 ticks, the lead is several blocks), so a
    // moving target gets almost no window at all. A standing target gets the full one: the only way out is to
    // stand, watch the cube freeze and sprint away at that very moment.
    public static final int CERO_LOCK_MOVING_TICKS = 3;      // window for a target that kept moving
    public static final int CERO_LOCK_STILL_TICKS = 12;      // window for a target that stood still
    public static final int CERO_STILL_WINDOW_TICKS = 10;    // how far back "recently" reaches
    public static final float CERO_STILL_DISTANCE = 1.0F;    // blocks moved in that window that count as fully moving
    public static final int CERO_HIT_DELAY_TICKS = 2;        // first damaging tick of the shot
    // The lead is capped by how fast the target CAN move, derived from its movement speed attribute (which already
    // includes Speed/Slowness effects, sprinting and modded buffs), so only noise is cut, never a real buff.
    public static final float CERO_ATTRIBUTE_TO_SPEED = 2.16F; // player attribute 0.1 ~ 0.216 blocks/tick walking
    public static final float CERO_LEAD_SPEED_MARGIN = 1.5F;   // sprint-jumping, soul speed, measurement noise
    public static final float CERO_LEAD_SPEED_FLOOR = 0.35F;   // blocks/tick, never cap below a vanilla sprint
    public static final float CERO_LEAD_SPEED_CEILING = 2.0F;  // blocks/tick (40 m/s): elytra, dashes; beyond this it is a teleport
    // Player positions reach the server in packets, so per-tick deltas jitter (0 one tick, double the next).
    // Velocity and acceleration are therefore measured over windows of position history, not single ticks.
    public static final int CERO_TRACK_TICKS = 20;           // history length; velocity uses the newer half, acceleration both halves
    public static final float CERO_MAX_ACCEL_LEAD = 1.5F;    // blocks: cap for the acceleration part of the lead

    // ===== Cero geometry, must match the renderer =====
    public static final float CERO_START = 2.05F;      // beam origin height above the feet
    public static final float CERO_FORWARD = 0.80F;    // beam origin offset in front of the body
    public static final float CERO_RADIUS = 0.5F;      // hit radius of the beam core
    // vertical aim limits in degrees: a little up, almost all the way down and even back under the boss,
    // so that standing below a hovering boss is not a safe spot
    public static final float CERO_PITCH_UP_LIMIT = 60.0F;
    public static final float CERO_PITCH_DOWN_LIMIT = 170.0F;
    public static final float CERO_RANGE = 128.0F;     // how far the beam deals damage
    // In RAGE the boss hovers: no gravity, and each teleport puts it in the air above the target's level
    public static final int CERO_HOVER_MIN = 3;               // blocks above the target's feet
    public static final int CERO_HOVER_MAX = 8;
    public static final int CERO_TELEPORT_SEARCH_HEIGHT = 24; // how far up/down to look for free space at the teleport spot
    // The beam gets stronger with every shot of the rage: shot N deals N / CERO_SHOTS_TO_KILL of the target's max
    // health, shot CERO_SHOTS_TO_KILL and later kill outright. Each beam hurts a player once.
    public static final int CERO_SHOTS_TO_KILL = 5;

    private int cooldownSonido = 0;
    private static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CURRENT_TARGET =
            SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CERO = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    // beam aim in degrees, synced so the client draws exactly the line the server tests against;
    // the server only updates it while the aim is not locked, so it freezes itself for LOCKED/SHOT
    private static final EntityDataAccessor<Float> CERO_YAW = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CERO_PITCH = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.FLOAT); // positive = up
    // length of the current LOCKED phase in ticks (depends on the target's stillness), synced for the renderer timeline
    private static final EntityDataAccessor<Integer> CERO_LOCK = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    private boolean rageTriggered = false;
    // tick when the current Cero phase started, updated on both sides in onSyncedDataUpdated
    private int ceroStartTick = 0;
    private int ceroStageTick = 0; // server only: position inside the Cero cycle
    private int ceroShots = 0;     // server only: beams fired in this rage, drives the damage ramp (saved in NBT)
    private final Set<UUID> ceroHitThisShot = new HashSet<>(); // server only: players already hurt by the current beam
    // per-tick mirror of the synced aim (current and previous tick) for interpolation
    private float ceroYaw = 0.0F;
    private float ceroYawO = 0.0F;
    private float ceroPitch = 0.0F;
    private float ceroPitchO = 0.0F;
    // server only: the target's recent positions (oldest first, one per tick) for the predictive aim
    private final Deque<Vec3> targetTrack = new ArrayDeque<>();
    private int trackedTargetId = -1;

    // Placeholder DELETE! diagnostics for the predictive aim
    private static final Logger LOGGER = LogUtils.getLogger();
    private Vec3 debugVelocity = Vec3.ZERO;
    private Vec3 debugPredicted = Vec3.ZERO;

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
        this.ceroShots = pCompound.getInt("scol.ceroShots");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("scol.stage", getStage().ordinal());
        pCompound.putInt("scol.cero", getCero().ordinal());
        pCompound.putBoolean("scol.rageTriggered", this.rageTriggered);
        pCompound.putInt("scol.ceroShots", this.ceroShots);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STAGE, Stage.DEFAULT.ordinal());
        this.entityData.define(CURRENT_TARGET, -1);
        this.entityData.define(CERO, Cero.PAUSE.ordinal());
        this.entityData.define(CERO_YAW, 0.0F);
        this.entityData.define(CERO_PITCH, 0.0F);
        this.entityData.define(CERO_LOCK, CERO_LOCK_STILL_TICKS);
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
                    (this.random.nextInt(8 - 5 + 1) + 5) * 2); // 5..8, doubled = 10, 12, 14 or 16
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        if (getStage().equals(Stage.RAGE) && !source.is(DamageTypes.GENERIC_KILL)) {
            return false;
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
        if (getPlayersAround(30).isEmpty()) {
            resetState(); // nobody left to fight: back to the calm boss
        }
        super.awardKillScore(ent, pScoreValue, source);
    }

    /**
     * Server: puts the boss back into its freshly spawned state. Stage, Cero cycle, target, aim, movement
     * tracking, teleport side bag, hover and health are all reset; synced fields reach the client on their own.
     */
    public void resetState() {
        // synced state
        setStage(Stage.DEFAULT);
        setCero(Cero.PAUSE);
        setSyncedTarget(null);
        this.entityData.set(CERO_YAW, 0.0F);
        this.entityData.set(CERO_PITCH, 0.0F);
        this.entityData.set(CERO_LOCK, CERO_LOCK_STILL_TICKS);

        // rage and the Cero cycle
        this.rageTriggered = false;
        this.ceroStageTick = 0;
        this.ceroShots = 0;
        this.ceroHitThisShot.clear();
        this.ceroStartTick = this.tickCount;
        this.ceroYaw = this.ceroYawO = 0.0F;
        this.ceroPitch = this.ceroPitchO = 0.0F;

        // predictive aim and teleport side bag
        this.targetTrack.clear();
        this.trackedTargetId = -1;
        this.beamSides.clear();
        this.lastBeamSide = null;
        this.cooldownSonido = 0;

        // hovering and AI
        this.setNoGravity(false);
        this.setDeltaMovement(Vec3.ZERO);
        this.setTarget(null);
        this.getNavigation().stop();

        this.heal(this.getMaxHealth());
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
        this.snapRotation(yaw, pitch);
    }

    /** Sets the whole rotation (head, body, previous tick) at once, so there is no smooth turn between ticks. */
    public void snapRotation(float yaw, float pitch) {
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

    /**
     * Client: in RAGE the boss only ever moves by instant teleports, so apply positions from the server at once.
     * Vanilla would slide it over 3 ticks, and a rotation-only packet (sent every tick while the aim tracks the
     * target) re-targets that slide at the entity's current, halfway position. The client copy then stayed stuck
     * between the old and the new spot until the next forced teleport packet, and the beam was drawn from there.
     */
    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
        if (this.getStage() == Stage.RAGE) {
            this.moveTo(x, y, z, yRot, xRot); // also resets the previous-tick position: no render interpolation
            this.lerpSteps = 0;
            return;
        }
        super.lerpTo(x, y, z, yRot, xRot, steps, teleport);
    }

    /** Instantly looks at the center of the block. */
    public void snapLookAt(BlockPos pos) {
        this.snapLookAt(Vec3.atCenterOf(pos));
    }

    /** Instantly looks at the entity's eyes. */
    public void snapLookAt(Entity target) {
        this.snapLookAt(target.getEyePosition());
    }

    // ===== Cero geometry (both sides) =====

    /** Horizontal angle of the beam in degrees (same convention as yRot). */
    public float getCeroYaw(float partialTick) {
        return Mth.rotLerp(partialTick, this.ceroYawO, this.ceroYaw);
    }

    /**
     * Vertical angle of the beam in degrees, positive = up. Below -90 the beam goes backwards under the boss,
     * which is what hits a target standing right beneath it.
     */
    public float getCeroPitch(float partialTick) {
        return Mth.clamp(Mth.lerp(partialTick, this.ceroPitchO, this.ceroPitch), -CERO_PITCH_DOWN_LIMIT, CERO_PITCH_UP_LIMIT);
    }

    /** Length of the current LOCKED phase in ticks (both sides). */
    public int getCeroLockTicks() {
        return this.entityData.get(CERO_LOCK);
    }

    /**
     * Server: how long the aim will stay locked for this target right now. A target that stood still for the last
     * CERO_STILL_WINDOW_TICKS gets the full window, one that covered CERO_STILL_DISTANCE or more almost none.
     * Running forward is handled by the lead; the short window is what stops "run, then brake at the lock".
     */
    private int computeCeroLockTicks() {
        Vec3[] track = this.targetTrack.toArray(new Vec3[0]);
        int window = Math.min(CERO_STILL_WINDOW_TICKS, track.length - 1);
        if (window < 1) {
            return CERO_LOCK_STILL_TICKS; // no history yet: be generous
        }
        Vec3 now = track[track.length - 1];
        Vec3 then = track[track.length - 1 - window];
        double moved = new Vec3(now.x - then.x, 0.0D, now.z - then.z).length() * CERO_STILL_WINDOW_TICKS / window;
        float moving = (float) Mth.clamp(moved / CERO_STILL_DISTANCE, 0.0D, 1.0D); // 0 = stood still, 1 = kept moving
        return Math.round(Mth.lerp(moving, CERO_LOCK_STILL_TICKS, CERO_LOCK_MOVING_TICKS));
    }

    /** Where the beam starts: in front of the face, rotated with the aim. */
    public Vec3 getCeroOrigin(float partialTick) {
        return ceroOriginAt(this.getPosition(partialTick), this.getCeroYaw(partialTick));
    }

    private static Vec3 ceroOriginAt(Vec3 feet, float yaw) {
        return feet.add(new Vec3(0.0D, CERO_START, CERO_FORWARD).yRot(-yaw * Mth.DEG_TO_RAD));
    }

    /** Unit direction of the beam. */
    public Vec3 getCeroDirection(float partialTick) {
        return Vec3.directionFromRotation(-this.getCeroPitch(partialTick), this.getCeroYaw(partialTick));
    }

    /** Server: records the target's position once per tick; the history restarts when the target changes. */
    private void trackTargetMotion(LivingEntity target) {
        if (target.getId() != this.trackedTargetId) {
            this.targetTrack.clear();
            this.trackedTargetId = target.getId();
        }
        this.targetTrack.addLast(target.position());
        while (this.targetTrack.size() > CERO_TRACK_TICKS + 1) {
            this.targetTrack.removeFirst();
        }
    }

    /** Average velocity (blocks/tick) between two samples of the track; vertical part only for airborne targets. */
    private static Vec3 velocityBetween(Vec3 from, Vec3 to, int ticks, boolean vertical) {
        return new Vec3(to.x - from.x, vertical ? to.y - from.y : 0.0D, to.z - from.z).scale(1.0D / ticks);
    }

    /** Creative flight or elytra: moves in all three axes and is not bound by the movement speed attribute. */
    private static boolean isAirborne(LivingEntity target) {
        return target.isFallFlying() || (target instanceof Player player && player.getAbilities().flying);
    }

    /**
     * Server: where the center of the target's hitbox will be in ticksAhead ticks if it keeps moving like now.
     * Velocity is the average over the newer half of the history, acceleration the change between the two halves,
     * so a single late or doubled movement packet barely moves the prediction. Grounded targets are predicted
     * horizontally only (jumps are noise), airborne ones in all three axes.
     */
    private Vec3 predictTargetCenter(LivingEntity target, float ticksAhead) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() / 2.0D, 0.0D);

        Vec3[] track = this.targetTrack.toArray(new Vec3[0]);
        int half = Math.min(CERO_TRACK_TICKS / 2, (track.length - 1) / 2); // ticks per half window
        if (half < 1) {
            return center; // not enough history yet: aim at the target itself
        }

        boolean vertical = isAirborne(target);
        Vec3 now = track[track.length - 1];
        Vec3 mid = track[track.length - 1 - half];
        Vec3 old = track[track.length - 1 - 2 * half];
        Vec3 velocity = velocityBetween(mid, now, half, vertical);
        Vec3 acceleration = velocity.subtract(velocityBetween(old, mid, half, vertical)).scale(1.0D / half);

        Vec3 accelLead = acceleration.scale(0.5D * ticksAhead * ticksAhead);
        if (accelLead.lengthSqr() > CERO_MAX_ACCEL_LEAD * CERO_MAX_ACCEL_LEAD) {
            accelLead = accelLead.normalize().scale(CERO_MAX_ACCEL_LEAD);
        }

        Vec3 lead = velocity.scale(ticksAhead).add(accelLead);

        // the target cannot outrun its own speed attribute, so anything beyond it is noise (lag spikes, teleports)
        double maxLead = maxLeadSpeed(target) * ticksAhead;
        if (lead.lengthSqr() > maxLead * maxLead) {
            lead = lead.normalize().scale(maxLead);
        }

        this.debugVelocity = velocity;      // Placeholder DELETE!
        this.debugPredicted = center.add(lead); // Placeholder DELETE!
        return center.add(lead);
    }

    /** Fastest plausible speed of the target in blocks/tick, from its current movement speed attribute. */
    private static double maxLeadSpeed(LivingEntity target) {
        if (isAirborne(target)) {
            return CERO_LEAD_SPEED_CEILING; // flight speed is not in the attribute: trust the measurement
        }
        double walk = target.getAttributeValue(Attributes.MOVEMENT_SPEED) * CERO_ATTRIBUTE_TO_SPEED;
        return Mth.clamp(walk * CERO_LEAD_SPEED_MARGIN, CERO_LEAD_SPEED_FLOOR, CERO_LEAD_SPEED_CEILING);
    }

    /** Server: points the beam at where the target will be when the beam starts to hurt. Not called while locked. */
    private void aimCeroAt(LivingEntity target) {
        // the lock length is not decided yet, so lead by what it would be if the aim locked right now
        Vec3 point = predictTargetCenter(target, computeCeroLockTicks() + CERO_HIT_DELAY_TICKS);

        // yaw from the feet: the origin is shifted along the yaw itself, so it does not change the yaw
        double dx = point.x - this.getX();
        double dz = point.z - this.getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;

        // pitch from the actual beam origin, which depends on that yaw. The horizontal part is SIGNED along the
        // facing direction: a target closer than the origin's forward offset (i.e. right under the boss) gives a
        // negative value and a pitch below -90, so the beam bends back under the boss instead of missing in front.
        Vec3 forward = Vec3.directionFromRotation(0.0F, yaw);
        Vec3 dir = point.subtract(ceroOriginAt(this.position(), yaw));
        double along = dir.x * forward.x + dir.z * forward.z;
        float pitch = (float) (Mth.atan2(dir.y, along) * Mth.RAD_TO_DEG);

        this.entityData.set(CERO_YAW, yaw);
        this.entityData.set(CERO_PITCH, pitch);
    }

    // Placeholder DELETE!
    private static String fmt(Vec3 v) {
        return String.format("(%.2f %.2f %.2f)", v.x, v.y, v.z);
    }

    /** True while the aim is frozen: the boss no longer tracks the target and the beam flies along the locked line. */
    public boolean isCeroLocked() {
        Cero cero = this.getCero();
        return cero == Cero.LOCKED || cero == Cero.SHOT;
    }

    /** Damage of the current beam for this target: a growing share of its max health, lethal from the Nth shot on. */
    private float ceroDamageFor(LivingEntity target) {
        if (this.ceroShots >= CERO_SHOTS_TO_KILL) {
            return Float.MAX_VALUE;
        }
        return target.getMaxHealth() * this.ceroShots / CERO_SHOTS_TO_KILL;
    }

    /** Server: hurts every survival player inside the beam cylinder, once per beam. */
    private void hitCeroTargets() {
        Vec3 from = this.getCeroOrigin(1.0F);
        Vec3 to = from.add(this.getCeroDirection(1.0F).scale(CERO_RANGE));
        AABB sweep = new AABB(from, to).inflate(CERO_RADIUS);

        for (Player player : this.level().getEntitiesOfClass(
                Player.class, sweep, p -> p.isAlive() && !p.isSpectator() && !p.isCreative())) {
            if (this.ceroHitThisShot.contains(player.getUUID())) {
                continue; // this beam already got them
            }
            // ray vs. the hitbox grown by the beam radius = cylinder vs. box, good enough here
            if (player.getBoundingBox().inflate(CERO_RADIUS).clip(from, to).isEmpty()) {
                continue;
            }
            this.ceroHitThisShot.add(player.getUUID());

            // scol:cero bypasses armor, effects, enchantments, resistance, shields, the hurt cooldown and
            // invulnerability, so the share of max health lands as is and a totem does not save from the kill shot
            float damage = ceroDamageFor(player);
            player.invulnerableTime = 0; // belt and braces next to the bypasses_cooldown tag
            player.hurt(this.damageSources().source(ScolDamageTypes.CERO, this), damage);

            // another mod intercepted the kill shot: the Cero does not allow that
            if (damage == Float.MAX_VALUE && player.isAlive()) {
                player.invulnerableTime = 0;
                player.hurt(this.damageSources().genericKill(), Float.MAX_VALUE);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            // Triggering rage
            if (this.getHealth() / this.getMaxHealth() < 0.25F && !this.rageTriggered) {
                setStage(Stage.RAGE);
                this.rageTriggered = true;
            }
            if (getStage().equals(Stage.RAGE)) {
                // hover: no gravity (synced flag) and no drift from knockback, AI or leftover momentum
                if (!this.isNoGravity()) {
                    this.setNoGravity(true);
                }
                this.setDeltaMovement(Vec3.ZERO);

                if (getSyncedTarget() == null || !getSyncedTarget().isAlive()) {
                    List<Player> players = this.getPlayersAround(30);
                    if (!players.isEmpty()) {
                        try {
                            for (Player playa : players) {
                                if (playa.isCreative()) continue;
                                setSyncedTarget(playa);
                            }
                        } finally {
                            if (getSyncedTarget() == null) {
                                resetState();
                                // TODO: Maybe add ability to kill from creative
                            }
                        }
                    }
                } else {
                    LivingEntity target = getSyncedTarget();

                    // server: lead the target until the aim locks; the synced aim then stays where it was
                    this.ceroYawO = this.ceroYaw;
                    this.ceroPitchO = this.ceroPitch;
                    if (!this.level().isClientSide) {
                        trackTargetMotion(target);
                        if (!isCeroLocked()) {
                            aimCeroAt(target);
                        }
                    }
                    this.ceroYaw = this.entityData.get(CERO_YAW);
                    this.ceroPitch = this.entityData.get(CERO_PITCH);

                    // both sides: the body follows the aim exactly, also keeps AI/look control from turning it
                    // (the head cannot bend past straight down, the beam can)
                    snapRotation(this.ceroYaw, Mth.clamp(-this.ceroPitch, -90.0F, 90.0F));

                    if (!this.level().isClientSide) {
                        int shotStart = CERO_CHARGE_TICKS + getCeroLockTicks();

                        if (this.ceroStageTick == 0) {
                            // moveTo also resets the previous-tick position, so there is no lerp from the old spot
                            Vec3 pos = Vec3.atBottomCenterOf(getRandomPositionForBeam());
                            moveTo(pos.x, pos.y, pos.z, this.getYRot(), this.getXRot());
                            this.playSound(ScolSounds.SONIDO, 50f, 1f);
                            this.getNavigation().stop();
                            setCero(Cero.CHARGING);
                        } else if (this.ceroStageTick == CERO_CHARGE_TICKS) {
                            // the dodge window is decided now, from how much the target moved lately
                            this.entityData.set(CERO_LOCK, computeCeroLockTicks());
                            setCero(Cero.LOCKED);
                        } else if (this.ceroStageTick == shotStart) {
                            this.ceroShots++;
                            this.ceroHitThisShot.clear();
                            setCero(Cero.SHOT);
                        } else if (this.ceroStageTick == shotStart + CERO_SHOT_TICKS) {
                            setCero(Cero.PAUSE);
                        }

                        // the beam hurts from the moment it has (almost) fully extended until it starts fading
                        int shotTick = this.ceroStageTick - shotStart;
                        if (shotTick == CERO_HIT_DELAY_TICKS) {
                            // Placeholder DELETE! how far the target's center is from the beam line when it starts to hurt
                            Vec3 center = target.position().add(0.0D, target.getBbHeight() / 2.0D, 0.0D);
                            Vec3 from = getCeroOrigin(1.0F);
                            Vec3 dir = getCeroDirection(1.0F);
                            Vec3 rel = center.subtract(from);
                            double miss = rel.subtract(dir.scale(rel.dot(dir))).length();
                            LOGGER.info("[cero] FIRE  target={} predicted={} error={} missFromBeam={} (hit if < ~0.8)",
                                    fmt(target.position()), fmt(this.debugPredicted),
                                    String.format("%.2f", center.distanceTo(this.debugPredicted)),
                                    String.format("%.2f", miss));
                        }
                        // Advance the cycle BEFORE dealing damage: a kill runs awardKillScore -> resetState() from inside
                        // hitCeroTargets(), and an increment after that would turn its fresh 0 into a 1, skipping the
                        // teleport/CHARGING step of every following cycle.
                        this.ceroStageTick++;
                        if (this.ceroStageTick >= shotStart + CERO_SHOT_TICKS + CERO_PAUSE_TICKS) {
                            this.ceroStageTick = 0;
                        }

                        if (shotTick >= CERO_HIT_DELAY_TICKS && shotTick < CERO_FIRE_TICKS + CERO_HOLD_TICKS) {
                            hitCeroTargets();
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
                        Player.class, box,
                        player -> player.isAlive() && !player.isSpectator() && !(player instanceof FakePlayer));
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

        // hovering: somewhere above the target's level, the boss has no gravity in RAGE
        int hover = Mth.nextInt(this.random, CERO_HOVER_MIN, CERO_HOVER_MAX);
        BlockPos candidate = targetPos
                .relative(side, distance)
                .relative(side.getClockWise(), sideways)
                .above(hover);

        return findFreePosition(candidate);
    }

    /**
     * Looks for free space near the candidate, closest to its Y first (0, +1, -1, +2, -2 ...), so the boss
     * never ends up inside terrain. Falls back to the candidate itself if everything around is solid.
     */
    private BlockPos findFreePosition(BlockPos candidate) {
        for (int dy = 0; dy <= CERO_TELEPORT_SEARCH_HEIGHT; dy++) {
            if (fitsAt(candidate.above(dy))) {
                return candidate.above(dy);
            }
            if (dy > 0 && fitsAt(candidate.below(dy))) {
                return candidate.below(dy);
            }
        }
        return candidate;
    }

    /** True when the boss's hitbox does not collide with blocks at this position. */
    private boolean fitsAt(BlockPos pos) {
        AABB box = this.getDimensions(Pose.STANDING).makeBoundingBox(Vec3.atBottomCenterOf(pos));
        return this.level().noCollision(this, box);
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
        LOCKED, // fully charged, aim frozen: the telegraph before the shot
        SHOT;

        private static final Cero[] VALUES = values(); // values() creates a new array on every call, so cache it

        public static Cero byId(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : PAUSE;
        }
    }

}
