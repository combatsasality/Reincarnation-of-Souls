package com.combatsasality.scol.entity;

import com.combatsasality.scol.items.Zangetsu;
import com.combatsasality.scol.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.List;


public class IchigoVizard extends Monster {
    private int cooldownSonido = 0;
//    private Stage stage = Stage.DEFAULT;
    private static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);
    private boolean rageTriggered = false;
    private static final EntityDataAccessor<Integer> CURRENT_TARGET =
            SynchedEntityData.defineId(IchigoVizard.class, EntityDataSerializers.INT);


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
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 10.0F, 1.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setStage(Stage.byId(pCompound.getInt("scol.stage")));
        this.rageTriggered = pCompound.getBoolean("scol.rageTriggered");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("scol.stage", getStage().ordinal());
        pCompound.putBoolean("scol.rageTriggered", this.rageTriggered);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STAGE, Stage.DEFAULT.ordinal());
        this.entityData.define(CURRENT_TARGET, -1);
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

    @Override
    public void tick() {
        super.tick();

        // Placeholder DELETE!
//        List<Player> playa = this.getPlayersAround(30);
//
//        if (!playa.isEmpty()) {
//            this.setTarget(playa.get(0));
//        }
        if (this.isAlive() && !this.level().isClientSide) {
            // Triggering rage
            if (this.getHealth() / this.getMaxHealth() < 0.25F && !this.rageTriggered) {
                setStage(Stage.RAGE);
                this.rageTriggered = true;
            } else if (getStage().equals(Stage.RAGE)) {
                System.out.println(getSyncedTarget() == null);
                if (getSyncedTarget() == null || !getSyncedTarget().isAlive()) {
                    List<Player> players = this.getPlayersAround(30);
                    if (!players.isEmpty()) {
                        setSyncedTarget(players.get(0));
                    }
                } else {

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
//
//    public enum Cero {
//        PAUSE,
//        CHARGING,
//        ;
//
//        private static final Cero[] VALUES = values(); // values() creates a new array on every call, so cache it
//
//        public static Cero byId(int id) {
//            return id >= 0 && id < VALUES.length ? VALUES[id] : PAUSE;
//        }
//    }

}
