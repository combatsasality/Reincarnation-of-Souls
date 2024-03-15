package com.combatsasality.scol.registries;

import com.combatsasality.scol.Main;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Supplier;

public abstract class AbstractRegistry<T extends IForgeRegistryEntry<T>> {
    protected static final String MODID = Main.MODID;
    private final DeferredRegister<T> register;

    public AbstractRegistry(IForgeRegistry<T> register) {
        this.register = DeferredRegister.create(register, MODID);
        this.register.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    protected void register(String name, Supplier<T> supplier) {
        this.register.register(name, supplier);
    }
//    protected void register(String name, NonNullFunction<? super DeferredRegister<T>, ? extends register> mapper) {
//
//    }
    protected DeferredRegister<T> getRegister() {
        return this.register;
    }
    private void onRegisterEvent() {}
}
