package com.combatsasality.scol.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ScolSounds extends AbstractRegistry<SoundEvent> {
    @ObjectHolder(value = MODID + ":music.metal_3", registryName = "sound_event")
    public static final SoundEvent MUSIC_METAL_3 = null;

    @ObjectHolder(value = MODID + ":music.silent_relapse", registryName = "sound_event")
    public static final SoundEvent MUSIC_SILENT_RELAPSE = null;

    @ObjectHolder(value = MODID + ":sonido", registryName = "sound_event")
    public static final SoundEvent SONIDO = null;

    public ScolSounds() {
        super(ForgeRegistries.SOUND_EVENTS);
        this.register("music.metal_3");
        this.register("music.silent_relapse");
        this.register("sonido");
    }

    private void register(String name) {
        super.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, name)));
    }
}
