package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.ScolCapabality;
import com.combatsasality.scol.entity.CustomItemEntity;
import com.combatsasality.scol.entity.projectile.PowerWaveEntity;
import com.combatsasality.scol.handlers.ItemTier;
import com.combatsasality.scol.registries.ScolSounds;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Zangetsu extends SwordItem {
    public Zangetsu() {
        super(ItemTier.FOR_ALL, 39, 0, new Properties().tab(Main.TAB).fireResistant().rarity(Rarity.EPIC));
    }
    public static boolean isBankai(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.Bankai");
    }
    public static void setBankai(ItemStack stack, boolean b) {
        stack.getOrCreateTag().putBoolean("scol.Bankai", b);
    }
    public static boolean isDeathModel(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.Death");
    }
    public static void setDeathModel(ItemStack stack, boolean b) {
        stack.getOrCreateTag().putBoolean("scol.Death", b);
    }
    public static boolean isDisableGravity(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.DisableGravity");
    }
    public static void setDisableGravity(ItemStack stack, boolean b) {
        stack.getOrCreateTag().putBoolean("scol.DisableGravity", b);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
        if (stack.getHoverName().getString().equals("combatsasality")) {
            tooltip.add(new TranslationTextComponent("tooltip.scol.zangetsu.combatsasality"));
        }
        super.appendHoverText(stack, world, tooltip, tooltipFlag);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClientSide()) {
            LazyOptional<ScolCapabality.DataCapability> capability = player.getCapability(ScolCapabality.NeedVariables);
            if (player.isCrouching()) {
                if (capability.map(capa -> capa.canUseBankai()).orElse(false) && capability.isPresent()) {
                    int levelBankai = capability.map(capa -> capa.getLevelBankai()).orElse(0);
                    if (levelBankai < 4) {
                        capability.ifPresent(capa -> capa.raiseLevelBankai());
                    }

                    if (levelBankai >= 3) {
                        capability.ifPresent(capa -> capa.setActiveBankai(!capa.isActiveBankai()));
                    } else {
                        capability.ifPresent(capa -> capa.setActiveBankaiTime(3600));
                        capability.ifPresent(capa -> capa.setCooldownBankai(24000 / (levelBankai+1)));
                    }
                }
            } else if (isBankai(player.getItemInHand(hand))) {
                Vector3d viewPos = player.pick(20.0D, 0.0F, false).getLocation();

                BlockState viewBlockOneUp = world.getBlockState(new BlockPos(viewPos.x,viewPos.y+1,viewPos.z));

                if (viewBlockOneUp.getBlock().equals(Blocks.AIR) || viewBlockOneUp.getBlock().equals(Blocks.WATER)) {
                    player.getCooldowns().addCooldown(this, 60 / player.getCapability(ScolCapabality.NeedVariables).map(capa -> capa.getLevelBankai()).orElse(1));
                    player.teleportTo(viewPos.x, viewPos.y, viewPos.z);
                    player.level.playSound(null, viewPos.x, viewPos.y, viewPos.z, ScolSounds.SONIDO, player.getSoundSource(), 1.0F, 1.0F);
                }
            } else {
                if (capability.map(capa -> capa.getLevelBankai()).orElse(0) == 4) {
                    player.getCooldowns().addCooldown(this, 150 / player.getCapability(ScolCapabality.NeedVariables).map(capa -> capa.getLevelBankai()).orElse(1));
                    PowerWaveEntity powerWaveEntity = new PowerWaveEntity(world, player);
                    powerWaveEntity.shootFromRotation();
                    world.addFreshEntity(powerWaveEntity);
                }
            }
        }
        return super.use(world, player, hand);
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (!world.isClientSide && entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            if (getOwner(stack).isEmpty()) {stack.setCount(0);return;}
            if (!getOwner(stack).equals(player.getGameProfile().getName())) {
                setDeathModel(stack, true);
                if (!stack.getHoverName().equals("combatsasality")) {
                    setDisableGravity(stack, true);
                }
                CustomItemEntity itemEntity = new CustomItemEntity(world, player.getX(), player.getY(), player.getZ(), stack.copy());
                stack.setCount(0);
                world.addFreshEntity(itemEntity);
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ANVIL_PLACE, entity.getSoundSource(), 1.0F, 1.0F);
                return;
            }
            if (EnchantmentHelper.hasVanishingCurse(stack)) {
                Map map = EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter((p_217012_0_) -> {
                    return !p_217012_0_.getKey().isCurse();
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                EnchantmentHelper.setEnchantments(map, stack);
            }
            LazyOptional<ScolCapabality.DataCapability> capability = entity.getCapability(ScolCapabality.NeedVariables);
            if (capability.map(capa -> capa.getCooldownBankai()).orElse(0) > 0) {
                capability.ifPresent(capa -> capa.consumeCooldownBankai(1));
            }
            boolean activeBankai = capability.map(capa -> capa.isActiveBankai()).orElse(false);
            int levelBankai = capability.map(capa -> capa.getLevelBankai()).orElse(0);

            if (activeBankai) {
                if (!isBankai(stack)) {
                    setBankai(stack, true); // bidlo code in the house :/
                }
                if (capability.map(capa -> capa.getActiveBankaiTime()).orElse(0) > 0) {
                    capability.ifPresent(capa -> capa.consumeActiveBankaiTime(1));
                }
                player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 200, levelBankai-1, false, false, false, null));
                player.addEffect(new EffectInstance(Effects.DIG_SPEED, 200, levelBankai-1, false, false, false, null));
                player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 200, levelBankai-1, false, false, false, null));
                player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 200, levelBankai-1, false, false, false, null));
                player.addEffect(new EffectInstance(Effects.REGENERATION, 200, levelBankai-1, false, false, false, null));
                player.getFoodData().eat(20, 1.0F);
                return;
            }

            setBankai(stack, false);

        }
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
    }

    public static String getOwner(ItemStack stack) {
        return stack.getOrCreateTag().getString("scol.Owner");
    }

    public static int getZangetsuModel(ItemStack stack) {
        boolean isDeathModel = isDeathModel(stack);
        boolean isBankai = isBankai(stack);
        String hoverName = stack.getHoverName().getString();

        if (isBankai && !isDeathModel) {
            return 6;
        }
        if (hoverName.equalsIgnoreCase("\u0434\u0436\u0443\u043C\u0430\u043C\u0431\u0435\u0430\u0431\u0441") && !isDeathModel) {
            return 4;
        }
        if (hoverName.equalsIgnoreCase("\u0434\u0436\u0443\u043C\u0430\u043C\u0431\u0435\u0430\u0431\u0441")) {
            return 5;
        }
        if (hoverName.equalsIgnoreCase("combatsasality") && isDeathModel) {
            return 3;
        }
        if (hoverName.equalsIgnoreCase("combatsasality")) {
            return 2;
        }
        if (isDeathModel) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isFoil(ItemStack p_77636_1_) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.isEnchanted();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
        if (slot.equals(EquipmentSlotType.MAINHAND)) {
            if (isBankai(stack)) {
                map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 99, AttributeModifier.Operation.ADDITION));
                map.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", 6, AttributeModifier.Operation.ADDITION));
            } else {
                map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.getDamage(), AttributeModifier.Operation.ADDITION));
                map.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.5, AttributeModifier.Operation.ADDITION));
            }
        }
        return map.build();

    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!player.level.isClientSide()) {
            if (entity.isAttackable() && !isBankai(stack)) {
                if (entity instanceof PartEntity) {
                    entity.hurt(DamageSource.playerAttack(player), 1 + (float) player.getAttributes().getValue(Attributes.ATTACK_DAMAGE));
                    return true;
                }
                for (LivingEntity livingEntity : player.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(1.0D))) {
                    if (livingEntity != player && (!(livingEntity instanceof ArmorStandEntity))) {
                        livingEntity.knockback(0.4F, (double) MathHelper.sin(player.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(player.yRot * ((float) Math.PI / 180F))));
                        livingEntity.hurt(DamageSource.playerAttack(player), 1 + (float) player.getAttributes().getValue(Attributes.ATTACK_DAMAGE));
                        EnchantmentHelper.doPostDamageEffects(livingEntity, player);
                    }
                }
                player.level.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                player.sweepAttack();
                return true;
            }
            if (entity.isAttackable() && isBankai(stack) && player.getCapability(ScolCapabality.NeedVariables).map(capa -> capa.getLevelBankai()).orElse(0) == 4) {
                entity.invulnerableTime = 0;
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}
