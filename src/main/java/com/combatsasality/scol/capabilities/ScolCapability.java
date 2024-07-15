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


        // Zangetsu
        boolean isHasZangetsu();
        void setHasZangetsu(boolean b);

        // Level Bankai
        int getLevelBankai();
        void raiseLevelBankai();

        // Cooldown Bankai
        int getCooldownBankai();
        void setCooldownBankai(int i);
        void consumeCooldownBankai(int i);

        // Active Bankai
        void setActiveBankai(boolean b);
        boolean isActiveBankai();
        void setActiveBankaiTime(int i);
        int getActiveBankaiTime();
        void consumeActiveBankaiTime(int i);

        // Bankai
        boolean canUseBankai();
    }

    public static class Capability implements IScolCapability {
        private int coolDownPhoenixRing = 0;
        private boolean hasZangetsu = false;
        private int levelBankai = 0;
        private int cooldownBankai = 0;
        private boolean activeBankai = false;
        private int activeBankaiTime = 0;


        @Override
        public Tag writeTag() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("coolDownPhoenixRing", coolDownPhoenixRing);
            compoundTag.putBoolean("hasZangetsu", hasZangetsu);
            compoundTag.putInt("levelBankai", levelBankai);
            compoundTag.putInt("cooldownBankai", cooldownBankai);
            compoundTag.putBoolean("activeBankai", activeBankai);
            compoundTag.putInt("activeBankaiTime", activeBankaiTime);
            return compoundTag;
        }

        @Override
        public void readTag(Tag nbt) {
            CompoundTag compoundTag = (CompoundTag) nbt;
            coolDownPhoenixRing = compoundTag.getInt("coolDownPhoenixRing");
            hasZangetsu = compoundTag.getBoolean("hasZangetsu");
            levelBankai = compoundTag.getInt("levelBankai");
            cooldownBankai = compoundTag.getInt("cooldownBankai");
            activeBankai = compoundTag.getBoolean("activeBankai");
            activeBankaiTime = compoundTag.getInt("activeBankaiTime");
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

        @Override
        public boolean isHasZangetsu() {
            return hasZangetsu;
        }

        @Override
        public void setHasZangetsu(boolean b) {
            hasZangetsu = b;
        }

        @Override
        public int getLevelBankai() {
            return levelBankai;
        }

        @Override
        public void raiseLevelBankai() {
            if (levelBankai >= 4) {
                return;
            }
            levelBankai++;
        }

        @Override
        public int getCooldownBankai() {
            return cooldownBankai;
        }

        @Override
        public void setCooldownBankai(int i) {
            cooldownBankai = i;
        }

        @Override
        public void consumeCooldownBankai(int i) {
            if (cooldownBankai < i || cooldownBankai < 0) {
                cooldownBankai = 0;
                return;
            }
            cooldownBankai -= i;
        }

        @Override
        public void setActiveBankai(boolean b) {
            activeBankai = b;
        }

        @Override
        public boolean isActiveBankai() {
            return activeBankai || activeBankaiTime > 0;
        }

        @Override
        public void setActiveBankaiTime(int i) {
            activeBankaiTime = i;
        }

        @Override
        public int getActiveBankaiTime() {
            return activeBankaiTime;
        }

        @Override
        public void consumeActiveBankaiTime(int i) {
            if (activeBankaiTime < i || activeBankaiTime < 0) {
                activeBankaiTime = 0;
                return;
            }
            activeBankaiTime -= i;
        }

        @Override
        public boolean canUseBankai() {
            return cooldownBankai == 0 || cooldownBankai == 0 && levelBankai >= 3;
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
