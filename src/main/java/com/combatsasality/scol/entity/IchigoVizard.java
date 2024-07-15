package com.combatsasality.scol.entity;

import com.combatsasality.scol.items.Zangetsu;
import com.combatsasality.scol.registries.ScolCapabilities;
import com.combatsasality.scol.registries.ScolEntities;
import com.combatsasality.scol.registries.ScolItems;
import com.combatsasality.scol.registries.ScolSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

public class IchigoVizard extends Monster {
    int cooldownSonido = 0;


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
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);

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
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public int getExperienceReward() {
        return 100;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        }
    }
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (this.canAttack((LivingEntity) entity)) {
            // TODO: add damage type bypass armor
            entity.hurt(this.damageSources().mobAttack(this), (this.random.nextInt((8 - 5 + 1) + 5) * 2));
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (this.random.nextFloat() > 0.66) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                float returnDamage = (float) (damage * 0.75);
                DamageSource returnSource = new DamageSource(source.typeHolder(), source.getEntity(), this, null);
                attacker.hurt(returnSource, returnDamage);
            }
        }

        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.FALLING_BLOCK)) {
            return false;
        }

        return super.hurt(source, damage);
    }

    @Override
    public void awardKillScore(Entity ent, int idk, DamageSource source) {
        this.heal(this.getMaxHealth());
        super.awardKillScore(ent, idk, source);
    }

    @Override
    protected void customServerAiStep() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive() && !this.level().isClientSide) {
            if (this.cooldownSonido != 0 ){
                this.cooldownSonido--;
            } else if (this.getTarget() != null) {
                LivingEntity target = this.getTarget();
                if (this.canAttack(target)) {
                    Path path = this.getNavigation().createPath(target, 1);
                    if (this.getVisibilityPercent(target) < 0.1 || (path == null || !path.canReach()) || Math.abs(Math.abs(target.getY())-Math.abs(this.getY())) > 1.5) {
                        this.setPos(target.getX(), target.getY(), target.getZ());
                        this.cooldownSonido= 200;
                        this.doHurtTarget(target);
                        this.playSound(ScolSounds.SONIDO, 50f, 1f);
                    }
                }
            }
            List<Player> players = this.getPlayersAround(30);
            if (!players.isEmpty()) {
                for (Player player : players) {
                    player.getAbilities().flying &= player.isCreative();
                    player.onUpdateAbilities();
                }
            }
        }
    }

    public List<Player> getPlayersAround(double range)  {
        AABB box = new AABB(this.getX() - range, this.getY() - range, this.getZ() - range, this.getX() + range, this.getY() + range, this.getZ() + range);
        return this.level().getEntitiesOfClass(Player.class, box, player -> !player.isSpectator() && !(player instanceof FakePlayer));
    }


    @Override
    protected void dropCustomDeathLoot(DamageSource source, int p_21386_, boolean p_21387_) {
        if (source.getEntity() instanceof ServerPlayer player) {
            player.getCapability(ScolCapabilities.SCOL_CAPABILITY).ifPresent(capa -> {
                if (!capa.isHasZangetsu()) {
                    ItemStack stack = new ItemStack(ScolItems.ZANGETSU);
                    Zangetsu.setDeathModel(stack, true);
                    Zangetsu.setDisableGravity(stack, true);
                    stack.getOrCreateTag().putString("scol.Owner", player.getGameProfile().getName());
                    CustomItemEntity dropped = new CustomItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack);
                    dropped.setPickupDelay(40);
                    if (this.level().addFreshEntity(dropped)) {
                        capa.setHasZangetsu(true);
                    }
                }
            });
        }
    }
}
