package com.combatsasality.scol.handlers;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;

//    public static final ForgeConfigSpec.ConfigValue<Boolean> UPDATE_CHECK;

    static {
        BUILDER.comment("General settings").push("general");

//        UPDATE_CHECK = BUILDER.comment("Check for updates").define("updateCheck", true);

        BUILDER.pop();
        CONFIG = BUILDER.build();
    }
}
