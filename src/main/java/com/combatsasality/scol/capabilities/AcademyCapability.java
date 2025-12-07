package com.combatsasality.scol.capabilities;

import com.combatsasality.scol.handlers.Academy;
import com.combatsasality.scol.registries.ScolCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AcademyCapability {
    public static ICapabilityProvider createProvider() {
        return new Provider();
    }

    public interface IAcademyCapability {
        Tag writeTag();

        void readTag(Tag nbt);

        void setAcademy(Academy academy);

        Academy getAcademy();
    }

    public static class Capability implements IAcademyCapability {
        private Academy academy = Academy.NULL;

        @Override
        public Tag writeTag() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("selected_academy", this.academy.getId());
            return compoundTag;
        }

        @Override
        public void readTag(Tag nbt) {
            CompoundTag compoundTag = (CompoundTag) nbt;
            this.academy = Academy.getFromId(compoundTag.getByte("selected_academy"));
        }

        @Override
        public void setAcademy(Academy academy) {
            this.academy = academy;
        }

        @Override
        public Academy getAcademy() {
            return academy;
        }
    }

    public static class Provider implements ICapabilitySerializable<Tag> {
        final LazyOptional<AcademyCapability.IAcademyCapability> optional;
        final AcademyCapability.IAcademyCapability instance;

        Provider() {
            instance = new AcademyCapability.Capability();
            optional = LazyOptional.of(() -> instance);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(
                net.minecraftforge.common.capabilities.@NotNull Capability<T> capability,
                @Nullable Direction direction) {

            return ScolCapabilities.ACADEMY_CAPABILITY.orEmpty(capability, this.optional);
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
