package dev.buildtool.satako;

public class EnergyProducer extends EnergyContainer {
    public EnergyProducer(int capacity, int maxTransfer, OnChanged onEnergyChange) {
        super(capacity, maxTransfer, onEnergyChange);
    }

    public EnergyProducer(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public void produceEnergy(int energy) {
        this.energy += energy;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
