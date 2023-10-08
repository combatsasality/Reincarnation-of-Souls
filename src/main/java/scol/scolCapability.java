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

public class scolCapability {

    @CapabilityInject(DataCapability.class)
    public static Capability<DataCapability> NeedVariables;

    public static String StrVariables = "scol:variables";

    public interface ICapability {
        CompoundNBT getNBT();
        void setNBT(CompoundNBT nbt);
        int getCoolDownPhoenixRing();
        void consumeCoolDownPhoenixRing(int i);
        void setCoolDownPhoenixRing(int i);

        boolean canUsePhoenixRing();
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
            return this.nbt.getInt("coolDownPhoenixRing");
        }

        @Override
        public void consumeCoolDownPhoenixRing(int i) {
            this.nbt.putInt("coolDownPhoenixRing", this.nbt.getInt("coolDownPhoenixRing") - i);
        }

        @Override
        public void setCoolDownPhoenixRing(int i) {
            this.nbt.putInt("coolDownPhoenixRing", i);
        }

        @Override
        public boolean canUsePhoenixRing() {
            return this.nbt.getInt("coolDownPhoenixRing") == 0;
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
