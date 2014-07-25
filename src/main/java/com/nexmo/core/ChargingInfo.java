package com.nexmo.core;

public class ChargingInfo {
    private final int unitsCharged;
    private final long timeUpdated;
    private final boolean lastCharge;

    public static ChargingInfo charge(final int unitsCharged) {
        return new ChargingInfo(unitsCharged, false);
    }

    public static ChargingInfo finalCharge(final int unitsCharged) {
        return new ChargingInfo(unitsCharged, true);
    }

    private ChargingInfo(final int unitsCharged, 
                         final boolean lastCharge) {
        this.unitsCharged = unitsCharged;
        this.timeUpdated = System.currentTimeMillis();
        this.lastCharge = lastCharge;
    }

    public int getUnitsCharged() {
        return this.unitsCharged;
    }

    public long getTimeUpdated() {
        return this.timeUpdated;
    }

    public boolean isLastCharge() {
        return this.lastCharge;
    }
}
