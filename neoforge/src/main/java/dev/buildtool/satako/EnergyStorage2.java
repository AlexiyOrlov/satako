package dev.buildtool.satako;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.energy.EnergyStorage;

public class EnergyStorage2 extends EnergyStorage {
    private OnChanged onEnergyChange;

    public EnergyStorage2(int capacity) {
        super(capacity);
    }

    public EnergyStorage2(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public EnergyStorage2(int capacity, int maxReceive, int maxExtract, OnChanged onEnergyChange) {
        super(capacity, maxReceive, maxExtract);
        this.onEnergyChange = onEnergyChange;
    }

    public EnergyStorage2(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public EnergyStorage2(int capacity, int maxTransfer, OnChanged onEnergyChange) {
        super(capacity, maxTransfer);
        this.onEnergyChange = onEnergyChange;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            energy = compoundTag.getInt("Energy");
        }
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("Energy", energy);
        return compoundTag;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        onEnergyChanged();
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        int received = super.receiveEnergy(toReceive, simulate);
        if (!simulate)
            onEnergyChanged();
        return received;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        int extracted = super.extractEnergy(toExtract, simulate);
        if (!simulate)
            onEnergyChanged();
        return extracted;
    }

    public void onEnergyChanged() {
        if (onEnergyChange != null)
            onEnergyChange.run(this);
    }

    public interface OnChanged {
        void run(EnergyStorage2 thisStorage);
    }

    public int selfExtract(int amount, boolean simulate)
    {
        int energyExtracted = Math.min(this.energy,  amount);
        if(!simulate)
        {
            energy-=energyExtracted;
            onEnergyChanged();
        }
        return energyExtracted;
    }
}
