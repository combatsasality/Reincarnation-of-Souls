package scol.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class AttackSpeedEnchant extends Enchantment {
    public AttackSpeedEnchant() {
        super(Rarity.VERY_RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
        this.setRegistryName("attack_speed_increase");
    }

    @Override
    public int getMaxLevel() {
        return 6;
    }
}

