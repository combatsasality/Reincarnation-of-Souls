package com.combatsasality.scol.proxy;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class CommonProxy {
    public CommonProxy() {
    }
    public void initEntityRendering() {
        // NO-OP
    }

    public void initPropertyOverrideRegistry(FMLClientSetupEvent event){
        // NO-OP
    }
    public void initAuxiliaryRender() {
        // NO-OP
    }
    public void loadComplete(FMLLoadCompleteEvent event) {
        // NO-OP
    }
    public void setupClient(FMLCommonSetupEvent event) {
        // NO-OP
    }
}
