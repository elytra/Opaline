package com.elytradev.opaline.util;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidTank;

public class ObservableFluidStorage extends FluidTank {
    private ArrayList<Runnable> listeners = new ArrayList<>();

    public ObservableFluidStorage(int capacity) {
        super(capacity);
        this.setCanFill(true);
        this.setCanDrain(true);
    }

    private void markDirty() {
        for(Runnable r : listeners) {
            r.run();
        }
    }

    public void listen(@Nonnull Runnable r) {
        listeners.add(r);
    }

    protected void onContentsChanged() {
        markDirty();
    }
}
