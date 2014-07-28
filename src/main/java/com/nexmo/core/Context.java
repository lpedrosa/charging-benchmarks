package com.nexmo.core;

import java.lang.StringBuilder;

public class Context {
    private final String sessionId;
    private final String connectionId;
    private final long createdAt;
    private final Type type;

    private int chargeCounter;
    private int totalCounter;

    private long start;
    private long end;
    private long lastUpdatedMillis;

    public Context(final String sessionId, 
                   final String connectionId,
                   final Type type) {
        this.sessionId = sessionId;
        this.connectionId = connectionId;
        this.type = type;

        this.createdAt = System.currentTimeMillis();
    }

    public Type getType() {
        return this.type;
    }

    public ContextStats update(ChargingInfo info) {
        if (info != null) {
            final int unitsCharged = info.getUnitsCharged();
            final long timeUpdated = info.getTimeUpdated();
            synchronized (this) {
                if (this.start == 0) {
                    this.start = timeUpdated;
                    this.chargeCounter -= unitsCharged;
                    this.totalCounter = unitsCharged;
                } else {
                    if (this.chargeCounter != 0) {
                        int delta = unitsCharged + this.chargeCounter;
                        this.totalCounter += delta;
                    } else {
                        this.totalCounter += unitsCharged;
                    }
                    this.chargeCounter = 0;
                }
                if (info.isLastCharge()) {
                    this.end = timeUpdated;
                }
                this.lastUpdatedMillis = timeUpdated;
                return new ContextStats(this.totalCounter, timeUpdated - this.start);
            }
        }
        return null;
    }

    public static enum Type {
        TTS, CALL;
    }

    @Override
    public String toString() {
        return new StringBuilder("Context[sessionId: ")
            .append(this.sessionId)
            .append(" connectionId: ")
            .append(this.connectionId)
            .append("]")
            .toString();
    }

    public static class ContextStats {
        private final int totalChargedUnits;
        private final long lengthSoFar;

        public ContextStats(final int totalChargedUnits, final long lengthSoFar) {
            this.totalChargedUnits = totalChargedUnits;
            this.lengthSoFar = lengthSoFar;
        }

        public int getTotalChargedUnits() {
            return this.totalChargedUnits;
        }

        public long getLengthSoFar() {
            return this.lengthSoFar;
        }

        @Override
        public String toString() {
            return new StringBuilder("Context Stats[totalChargedUnits: ")
                .append(this.totalChargedUnits)
                .append(" lengthSoFar: ")
                .append(this.lengthSoFar)
                .append("]")
                .toString();
        }
    }
}
