package scol;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScolCapabality {

    @CapabilityInject(DataCapability.class)
    public static Capability<DataCapability> NeedVariables;

    public static String StrVariables = "scol:variables";

    public interface ICapability {
        CompoundNBT getNBT();
        void setNBT(CompoundNBT nbt);

        // Phoenix Ring
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

    public static class DataCapability implements ICapability {
        private CompoundNBT nbt = new CompoundNBT();

        @Override
        public CompoundNBT getNBT() {
            return this.nbt;
        }

        @Override
        public void setNBT(CompoundNBT nbt) {
            this.nbt = nbt;
        }

        @Override
        public int getCoolDownPhoenixRing() {
            return this.nbt.getInt("CooldownPhoenixRing");
        }

        @Override
        public void consumeCoolDownPhoenixRing(int i) {
            if (this.nbt.getInt("CooldownPhoenixRing") < i) {
                this.nbt.putInt("CooldownPhoenixRing", 0);
                return;
            }
            this.nbt.putInt("CooldownPhoenixRing", this.nbt.getInt("CooldownPhoenixRing") - i);
        }

        @Override
        public void setCoolDownPhoenixRing(int i) {
            this.nbt.putInt("CooldownPhoenixRing", i);
        }

        @Override
        public boolean canUsePhoenixRing() {
            return this.nbt.getInt("CooldownPhoenixRing") == 0;
        }

        @Override
        public boolean isHasZangetsu() {
            return this.nbt.getBoolean("HasZangetsu");
        }

        @Override
        public void setHasZangetsu(boolean b) {
            this.nbt.putBoolean("HasZangetsu", b);
        }

        @Override
        public int getLevelBankai() {
            return this.nbt.getInt("LevelBankai");
        }

        @Override
        public void raiseLevelBankai() {
            if (this.nbt.getInt("LevelBankai") >= 4) {
                return;
            }
            this.nbt.putInt("LevelBankai", this.nbt.getInt("LevelBankai")+1);
        }

        @Override
        public int getCooldownBankai() {
            return this.nbt.getInt("CooldownBankai");
        }

        @Override
        public void setCooldownBankai(int i) {
            this.nbt.putInt("CooldownBankai", i);
        }

        @Override
        public void consumeCooldownBankai(int i) {
            if (this.nbt.getInt("CooldownBankai") < i || this.nbt.getInt("CooldownBankai") < 0) {
                this.nbt.putInt("CooldownBankai", 0);
                return;
            }
            this.nbt.putInt("CooldownBankai", this.nbt.getInt("CooldownBankai") - i);
        }

        @Override
        public void setActiveBankai(boolean b) {
            this.nbt.putBoolean("ActiveBankai", b);
        }

        @Override
        public boolean isActiveBankai() {
            return this.nbt.getBoolean("ActiveBankai") || this.nbt.getInt("ActiveBankaiTime") > 0;
        }

        @Override
        public void setActiveBankaiTime(int i) {
            this.nbt.putInt("ActiveBankaiTime", i);
        }

        @Override
        public int getActiveBankaiTime() {
            return this.nbt.getInt("ActiveBankaiTime");
        }

        @Override
        public void consumeActiveBankaiTime(int i) {
            this.nbt.putInt("ActiveBankaiTime", this.nbt.getInt("ActiveBankaiTime") - i);
        }

        @Override
        public boolean canUseBankai() {
            return this.nbt.getInt("CooldownBankai") == 0 || this.nbt.getInt("CooldownBankai") == 0 && this.nbt.getInt("LevelBankai") >= 3;
        }

        public static class Storage implements Capability.IStorage<DataCapability> {

            @Nullable
            @Override
            public INBT writeNBT(Capability<DataCapability> capability, DataCapability instance, Direction side) {
                return instance.getNBT();
            }

            @Override
            public void readNBT(Capability<DataCapability> capability, DataCapability instance, Direction side, INBT nbt) {
                instance.setNBT((CompoundNBT) nbt);
            }
        }

        public static class Provider implements ICapabilitySerializable<INBT> {
            private final DataCapability Var = new DataCapability();
            private final Capability.IStorage<DataCapability> storage = NeedVariables.getStorage();

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap.equals(NeedVariables)) return LazyOptional.of(() -> Var).cast();
                else return LazyOptional.empty();
            }

            @Override
            public INBT serializeNBT() {
                return storage.writeNBT(NeedVariables, Var, null);
            }

            @Override
            public void deserializeNBT(INBT nbt) {
                storage.readNBT(NeedVariables, Var, null, nbt);
            }
        }
    }

}
