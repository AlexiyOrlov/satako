package dev.buildtool.satako;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.energy.EnergyStorage;

public class EnergyStorage2 extends EnergyStorage {
    public EnergyStorage2(int capacity) {
        super(capacity);
    }

    public EnergyStorage2(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public EnergyStorage2(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public EnergyStorage2(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        if(nbt instanceof CompoundTag compoundTag)
        {
            energy=compoundTag.getInt("Energy");
        }
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag=new CompoundTag();
        compoundTag.putInt("Energy",energy);
        return compoundTag;
    }

    public void setEnergy(int energy)
    {
        this.energy=energy;
        onEnergyChanged();
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if(!simulate)
            onEnergyChanged();
        return super.receiveEnergy(toReceive, simulate);
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if(!simulate)
            onEnergyChanged();
        return super.extractEnergy(toExtract, simulate);
    }

    public void onEnergyChanged()
    {

    }
}
