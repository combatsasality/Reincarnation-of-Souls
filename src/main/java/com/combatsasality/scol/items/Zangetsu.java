package com.combatsasality.scol.items;

import com.combatsasality.scol.Main;
import com.combatsasality.scol.capabilities.ScolCapability;
import com.combatsasality.scol.entity.CustomItemEntity;
import com.combatsasality.scol.handlers.ItemTier;
import com.combatsasality.scol.items.generic.ITab;
import com.combatsasality.scol.registries.ScolCapabilities;
import com.combatsasality.scol.registries.ScolSounds;
import com.combatsasality.scol.registries.ScolTabs;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Zangetsu extends SwordItem implements ITab {
    public Zangetsu() {
        super(ItemTier.FOR_ALL, 39, 0, new Properties().fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getHoverName().getString().equals("combatsasality")) {
            tooltip.add(Component.translatable("tooltip.scol.zangetsu.combatsasality"));
        }
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return ScolTabs.MAIN;
    }

    @Override
    public List<ItemStack> getCreativeTabStacks() {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putString("scol.Owner", Minecraft.getInstance().player.getGameProfile().getName());
        return ImmutableList.of(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.isEnchanted();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            LazyOptional<ScolCapability.IScolCapability> capability = player.getCapability(ScolCapabilities.SCOL_CAPABILITY);
            if (player.isCrouching()) {
                capability.ifPresent(capa -> {
                    if (capa.canUseBankai()) {
                        if (capa.getLevelBankai() < 4) {
                            capa.raiseLevelBankai();
                        }
                        if (capa.getLevelBankai() >= 3) {
                            capa.setActiveBankai(!capa.isActiveBankai());
                        } else {
                            capa.setActiveBankaiTime(3600);
                            capa.setCooldownBankai(24000 / (capa.getLevelBankai()+1));
                        }
                    }
                });
            } else if (isBankai(player.getItemInHand(hand))) {
                Vec3 viewPos = player.pick(20.0D, 0.0F, false).getLocation();
                BlockState viewBlockOneUp = level.getBlockState(new BlockPos((int) viewPos.x, (int) (viewPos.y+1), (int) viewPos.z));
                if (viewBlockOneUp.getBlock().equals(Blocks.AIR) || viewBlockOneUp.getBlock().equals(Blocks.WATER)) {
                    player.getCooldowns().addCooldown(this, 60 / capability.map(capa -> capa.getLevelBankai()).orElse(1));
                    player.teleportTo(viewPos.x, viewPos.y, viewPos.z);
                    player.level().playSound(null, viewPos.x, viewPos.y, viewPos.z, ScolSounds.SONIDO, player.getSoundSource(), 1.0F, 1.0F);
                }
            } else {
                if (capability.map(capa -> capa.getLevelBankai()).orElse(0) == 4) {
                    player.getCooldowns().addCooldown(this, 150 / capability.map(capa -> capa.getLevelBankai()).orElse(1));
                    // TODO: add power wave entity here
                }
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) return;
        if (getOwner(stack).isEmpty()) {stack.setCount(0);return;}
        if (!getOwner(stack).equals(player.getGameProfile().getName())) {
            setDeathModel(stack, true);
            if (!stack.getHoverName().equals("combatsasality")) {
                setDisableGravity(stack, true);
            }
            CustomItemEntity itemEntity = new CustomItemEntity(level, player.getX(), player.getY(), player.getZ(), stack.copy());
            stack.setCount(0);
            level.addFreshEntity(itemEntity);
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ANVIL_PLACE, entity.getSoundSource(), 1.0F, 1.0F);
            return;
        }
        stack.getAllEnchantments().forEach((enchantment, integer) -> {
            if (enchantment.isCurse()) {
                stack.enchant(enchantment, 0);
            }
        });
        LazyOptional<ScolCapability.IScolCapability> capability = entity.getCapability(ScolCapabilities.SCOL_CAPABILITY);
        capability.ifPresent(capa -> {
            if (capa.getCooldownBankai() > 0) {
                capa.consumeCooldownBankai(1);
            }

            if (capa.isActiveBankai()) {
                if (!isBankai(stack)) {
                    setBankai(stack, true);
                }
                if (capa.getActiveBankaiTime() > 0) {
                    capa.consumeActiveBankaiTime(1);
                }
                int levelBankai = capa.getLevelBankai();
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, levelBankai-1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, levelBankai-1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, levelBankai-1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, levelBankai-1, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, levelBankai-1, false, false, false));
                player.getFoodData().eat(20, 20.0F);
                return;
            }
            setBankai(stack, false);
        });
        super.inventoryTick(stack, level, entity, p_41407_, p_41408_);
    }

    public static String getOwner(ItemStack stack) {
        return stack.getOrCreateTag().getString("scol.Owner");
    }
    public static boolean isBankai(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.Bankai");
    }
    public static void setBankai(ItemStack stack, boolean bankai) {
        stack.getOrCreateTag().putBoolean("scol.Bankai", bankai);
    }
    public static void setDeathModel(ItemStack stack, boolean b) {
        stack.getOrCreateTag().putBoolean("scol.Death", b);
    }
    public static boolean isDeathModel(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.Death");
    }
    public static boolean isDisableGravity(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("scol.DisableGravity");
    }
    public static void setDisableGravity(ItemStack stack, boolean disableGravity) {
        stack.getOrCreateTag().putBoolean("scol.DisableGravity", disableGravity);
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

    public void registerVariants() {
        ItemProperties.register(this, new ResourceLocation(Main.MODID, "zangetsu_model"), (stack, world, entity, number) -> getZangetsuModel(stack));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
        if (slot.equals(EquipmentSlot.MAINHAND)) {
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
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            if (entity.isAttackable() && !isBankai(stack)) {
                if (entity instanceof PartEntity) {
                    entity.hurt(entity.damageSources().playerAttack(player), 1 + (float) player.getAttributes().getValue(Attributes.ATTACK_DAMAGE));
                    return true;
                }
                for (LivingEntity livingEntity : player.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(1.0D))) {
                    if (livingEntity != player && (!(livingEntity instanceof ArmorStand))) {
                        livingEntity.knockback(0.4F, (double) Mth.sin(player.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(player.getYRot() * ((float) Math.PI / 180F))));
                        livingEntity.hurt(livingEntity.damageSources().playerAttack(player), 1 + (float) player.getAttributes().getValue(Attributes.ATTACK_DAMAGE));
                        player.doEnchantDamageEffects(livingEntity, player);
                    }
                }
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                player.sweepAttack();
                return true;
            }
            if (entity.isAttackable() && isBankai(stack) && player.getCapability(ScolCapabilities.SCOL_CAPABILITY).map(capa -> capa.getLevelBankai()).orElse(0) == 4) {
                entity.invulnerableTime = 0;
            }
        }


        return super.onLeftClickEntity(stack, player, entity);
    }
}
