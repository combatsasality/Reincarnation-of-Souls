package com.combatsasality.scol.registries;

import com.combatsasality.scol.capabilities.ScolCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ScolCapabilities {
    public static final Capability<ScolCapability.IScolCapability> SCOL_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});
}
