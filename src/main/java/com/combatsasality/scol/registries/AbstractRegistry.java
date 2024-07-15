package com.combatsasality.scol.registries;

import com.combatsasality.scol.Main;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Supplier;

public class AbstractRegistry<T> {
    protected static final String MODID = Main.MODID;
    private final DeferredRegister<T> register;

    protected AbstractRegistry(ResourceKey<Registry<T>> registry) {
        this.register = DeferredRegister.create(registry, MODID);
        this.register.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterEvent);
    }

    protected AbstractRegistry(IForgeRegistry<T> registry) {
        this(registry.getRegistryKey());
    }

    protected void register(String name, Supplier<T> supplier) {
        this.register.register(name, supplier);
    }

    private void onRegisterEvent(RegisterEvent event) {
        if (event.getRegistryKey() == this.register.getRegistryKey()) {
            this.onRegister(event);
        }
    }

    protected void onRegister(RegisterEvent event) {
        // NO-OP
    }

}
