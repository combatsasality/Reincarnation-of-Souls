package com.combatsasality.scol.registries;


import com.combatsasality.scol.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ScolDamageTypes {
    public static final ResourceKey<DamageType> FROSTMOURNE = register("frostmourne");
    public static final ResourceKey<DamageType> ICHIGO = register("ichigo");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Main.MODID, name));
    }
}
