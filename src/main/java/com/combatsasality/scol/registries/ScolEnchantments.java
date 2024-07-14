package com.combatsasality.scol.registries;

import com.combatsasality.scol.enchantment.AttackSpeedEnchant;
import com.combatsasality.scol.enchantment.VampiricEnchant;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolEnchantments extends AbstractRegistry<Enchantment> {

    @ObjectHolder(value = MODID + ":vampiric_enchant", registryName = "enchantment")
    public static final Enchantment VAMPIRIC_ENCHANT = null;
    @ObjectHolder(value = MODID + ":attack_speed_increase", registryName = "enchantment")
    public static final Enchantment ATTACK_SPEED_INCREASE = null;


    public ScolEnchantments() {
        super(ForgeRegistries.ENCHANTMENTS);
        register("vampiric_enchant", VampiricEnchant::new);
        register("attack_speed_increase", AttackSpeedEnchant::new);
    }

}
