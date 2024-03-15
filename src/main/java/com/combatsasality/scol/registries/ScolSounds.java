package com.combatsasality.scol.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolSounds extends AbstractRegistry<SoundEvent> {
    @ObjectHolder(MODID + ":music.metal_3")
    public static SoundEvent MUSIC_METAL_3;
    @ObjectHolder(MODID + ":music.silent_relapse")
    public static SoundEvent MUSIC_SILENT_RELAPSE;
    @ObjectHolder(MODID + ":sonido")
    public static SoundEvent SONIDO;

    public ScolSounds() {
        super(ForgeRegistries.SOUND_EVENTS);
        register("music.metal_3");
        register("music.silent_relapse");
        register("sonido");
    }

    private void register(String name) {
        super.register(name, () -> new SoundEvent(new ResourceLocation(MODID, name)));
    }
}
