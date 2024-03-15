package scol.registries;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import scol.enchantment.AttackSpeedEnchant;
import scol.enchantment.SoulCatcherEnchant;
import scol.enchantment.VampiricEnchant;

public class ScolEnchantments extends AbstractRegistry<Enchantment> {
    @ObjectHolder(MODID + ":vampirism_enchant")
    public static VampiricEnchant VAMPIRISM_ENCHANT;
    @ObjectHolder(MODID + ":attack_speed_increase")
    public static AttackSpeedEnchant ATTACK_SPEED_INCREASE;
    @ObjectHolder(MODID + ":soul_catcher")
    public static SoulCatcherEnchant SOUL_CATCHER;

    public ScolEnchantments() {
        super(ForgeRegistries.ENCHANTMENTS);
        register("vampirism_enchant", VampiricEnchant::new);
        register("attack_speed_increase", AttackSpeedEnchant::new);
        register("soul_catcher", SoulCatcherEnchant::new);
    }
}
