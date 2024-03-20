package com.combatsasality.scol.registries;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolPotions extends AbstractRegistry<Potion> {
    @ObjectHolder(MODID + ":hero_of_village")
    public static final Potion HERO_OF_VILLAGE = new Potion("hero_of_village", new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 3600));
    @ObjectHolder(MODID + ":long_hero_of_village")
    public static final Potion LONG_HERO_OF_VILLAGE = new Potion("hero_of_village", new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 9600));
    @ObjectHolder(MODID + ":strong_hero_of_village")
    public static final Potion STRONG_HERO_OF_VILLAGE = new Potion("hero_of_village", new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 1800, 1));

    public ScolPotions() {
        super(ForgeRegistries.POTION_TYPES);
        register("hero_of_village", () -> HERO_OF_VILLAGE);
        register("long_hero_of_village", () -> LONG_HERO_OF_VILLAGE);
        register("strong_hero_of_village", () -> STRONG_HERO_OF_VILLAGE);
    }
}
