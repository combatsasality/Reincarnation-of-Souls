package com.combatsasality.scol.capabilities;


import com.combatsasality.scol.registries.ScolCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScolCapability {


    public static ICapabilityProvider createProvider() {
        return new Provider();
    }

    public interface IScolCapability {
        Tag writeTag();
        void readTag(Tag nbt);

        int getCoolDownPhoenixRing();
        void consumeCoolDownPhoenixRing(int i);
        void setCoolDownPhoenixRing(int i);
        boolean canUsePhoenixRing();
    }

    public static class Capability implements IScolCapability {
        private int coolDownPhoenixRing = 0;


        @Override
        public Tag writeTag() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("coolDownPhoenixRing", coolDownPhoenixRing);
            return compoundTag;
        }

        @Override
        public void readTag(Tag nbt) {
            CompoundTag compoundTag = (CompoundTag) nbt;
            coolDownPhoenixRing = compoundTag.getInt("coolDownPhoenixRing");
        }

        @Override
        public int getCoolDownPhoenixRing() {
            return coolDownPhoenixRing;
        }

        @Override
        public void consumeCoolDownPhoenixRing(int i) {
            if (coolDownPhoenixRing < i) {
                coolDownPhoenixRing = 0;
                return;
            }
            coolDownPhoenixRing -= i;
        }

        @Override
        public void setCoolDownPhoenixRing(int i) {
            coolDownPhoenixRing = i;
        }

        @Override
        public boolean canUsePhoenixRing() {
            return coolDownPhoenixRing == 0;
        }
    }

    public static class Provider implements ICapabilitySerializable<Tag> {
        final LazyOptional<IScolCapability> optional;
        final IScolCapability instance;

        Provider() {
            instance = new Capability();
            optional = LazyOptional.of(() -> instance);
        }


        @Override
        public @NotNull <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.@NotNull Capability<T> capability, @Nullable Direction direction) {

            return ScolCapabilities.SCOL_CAPABILITY.orEmpty(capability, this.optional);
        }

        @Override
        public Tag serializeNBT() {
            return this.instance.writeTag();
        }

        @Override
        public void deserializeNBT(Tag tag) {
            this.instance.readTag(tag);
        }
    }
}
