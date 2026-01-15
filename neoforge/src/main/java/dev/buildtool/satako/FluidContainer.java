package dev.buildtool.satako;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class FluidContainer extends FluidTank {
    private OnChange onChangedAction;
    private final boolean canExtract;

    public FluidContainer(int capacity, boolean canExtract) {
        this(capacity, fluidStack -> true, canExtract);
    }

    public FluidContainer(int capacity, Predicate<FluidStack> validator, boolean canExtract) {
        super(capacity, validator);
        this.canExtract = canExtract;
    }

    public FluidContainer(int capacity, boolean canExtract, OnChange onChangedAction) {
        super(capacity);
        this.canExtract = canExtract;
        this.onChangedAction = onChangedAction;
    }

    public FluidContainer(int capacity, Predicate<FluidStack> validator, boolean canExtract, OnChange onChangedAction) {
        super(capacity, validator);
        this.canExtract = canExtract;
        this.onChangedAction = onChangedAction;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (!canExtract)
            return FluidStack.EMPTY;
        return super.drain(maxDrain, action);
    }

    public FluidStack forceExtract(int max, FluidAction action) {
        return super.drain(max, action);
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        if (onChangedAction != null)
            onChangedAction.run(this);
    }

    @FunctionalInterface
    public interface OnChange {
        void run(FluidContainer thisContainer);
    }

}
