package com.combatsasality.scol.entity;

import com.combatsasality.scol.ScolCapabality;
import com.combatsasality.scol.handlers.HelpHandler;
import com.combatsasality.scol.items.Zangetsu;
import com.combatsasality.scol.registries.ScolEntities;
import com.combatsasality.scol.registries.ScolItems;
import com.combatsasality.scol.registries.ScolSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

public class IchigoVizard extends MonsterEntity {
    int cooldownSonido = 0;

//    @ObjectHolder(Main.MODID + ":ichigo_vizard")
//    public static EntityType<IchigoVizard> TYPE;


    public IchigoVizard(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        ItemStack stack = new ItemStack(ScolItems.ZANGETSU);
        Zangetsu.setBankai(stack, true);
        this.setItemInHand(Hand.MAIN_HAND, stack);
    }

    public IchigoVizard(World worldIn, double x, double y, double z) {
        this(ScolEntities.ICHIGO_VIZARD, worldIn);
        this.setPos(x, y, z);
        this.yRot = this.random.nextFloat() * 360.0F;
    }
    public IchigoVizard(World worldIn, BlockPos pos) {
        this(worldIn, pos.getX(), pos.getY(), pos.getZ());
    }

    public IchigoVizard(World worldIn, Vector3d pos) {
        this(worldIn, pos.x, pos.y, pos.z);
    }

    private final ServerBossInfo bossEvent = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true);

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MAX_HEALTH, 1000.0d)
                .add(Attributes.ATTACK_DAMAGE, 0.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.5d)
                .add(Attributes.ARMOR, 15.0d)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100.0d);
    }


    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    }
    @Override
    protected int getExperienceReward(PlayerEntity p_70693_1_) {
        return 100;
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity entity) {
        super.startSeenByPlayer(entity);
        this.bossEvent.addPlayer(entity);
    }
    @Override
    public void stopSeenByPlayer(ServerPlayerEntity entity) {
        super.stopSeenByPlayer(entity);
        this.bossEvent.removePlayer(entity);
    }
    @Override
    public HandSide getMainArm() {
        return HandSide.RIGHT;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.remove();
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
            entity.hurt(DamageSource.mobAttack(this).bypassArmor(), HelpHandler.getRandomInt(this.random, 5,8) * 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (this.random.nextFloat() > 0.66) {
            if (source.getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) source.getEntity();
                float returndamage = (float) (damage * 0.75);
                DamageSource returnsource = new EntityDamageSource("ReturnDamageIchigo", this);
                if (source.isBypassArmor()) {
                    attacker.hurt(returnsource.bypassArmor(), returndamage);
                } else if (source.isBypassInvul()) {
                    attacker.hurt(returnsource.bypassInvul(), returndamage);
                } else if (source.isFire()) {
                    attacker.hurt(returnsource.setIsFire(), returndamage);
                } else if (source.isExplosion()) {
                    attacker.hurt(returnsource.setExplosion(), returndamage);
                } else if (source.isMagic()) {
                    attacker.hurt(returnsource.setMagic(), returndamage);
                } else if (source.isBypassMagic()) {
                    attacker.hurt(returnsource.bypassMagic(), returndamage);
                } else {
                    attacker.hurt(returnsource, returndamage);
                }
            }
        }
        if (source == DamageSource.IN_WALL || source == DamageSource.ON_FIRE || source == DamageSource.IN_FIRE || source == DamageSource.FALL || source == DamageSource.FALLING_BLOCK) {
            return false;
        }

        return super.hurt(source, damage);
    }

    @Override
    public void awardKillScore(Entity ent, int xeze, DamageSource source) {
        if (!source.getMsgId().equals("ReturnDamageIchigo")) this.heal(this.getMaxHealth());
        super.awardKillScore(ent, xeze, source);
    }

    @Override
    protected void customServerAiStep() {
        this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive() && !this.level.isClientSide()) {
            if (this.cooldownSonido != 0) {
                this.cooldownSonido--;
            } else if (this.getTarget() != null) {
                LivingEntity target = this.getTarget();
                if (this.canAttack(target)) {
                    Path path = this.getNavigation().createPath(target, 1);
                    if (!this.canSee(target) || (path == null || !path.canReach()) || Math.abs(Math.abs(target.getY()) - Math.abs(this.getY())) > 1.5) {
                        this.setPos(target.getX(), target.getY(), target.getZ());
                        this.cooldownSonido = 200;
                        this.doHurtTarget(target);
                        this.playSound(ScolSounds.SONIDO, 50f, 1f);
                    }
                }
            }
            List<PlayerEntity> players = this.getPlayersAround(30);
            if (!players.isEmpty()) {
                for (PlayerEntity player : players) {
                    player.abilities.flying &= player.isCreative();
                    player.onUpdateAbilities();
                }
            }
        }
    }

    public List<PlayerEntity> getPlayersAround(double range)  {
        AxisAlignedBB box = new AxisAlignedBB(this.getX() - range, this.getY() - range, this.getZ() - range, this.getX() + range, this.getY() + range, this.getZ() + range);
        return this.level.getEntitiesOfClass(PlayerEntity.class, box, player -> !player.isSpectator() && !(player instanceof FakePlayer));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int p_213333_2_, boolean p_213333_3_) {
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
            if (!player.getCapability(ScolCapabality.NeedVariables).map(capa -> capa.isHasZangetsu()).orElse(false)) {
                ItemStack stack = new ItemStack(ScolItems.ZANGETSU);
                Zangetsu.setDeathModel(stack, true);
                Zangetsu.setDisableGravity(stack, true);
                stack.getOrCreateTag().putString("scol.Owner", player.getGameProfile().getName());
                CustomItemEntity dropped = new CustomItemEntity(this.level, this.getX(), this.getY(), this.getZ(), stack);
                dropped.setPickupDelay(40);
                this.level.addFreshEntity(dropped);
                player.getCapability(ScolCapabality.NeedVariables).ifPresent(capa -> capa.setHasZangetsu(true));
            }
        }
    }
}
