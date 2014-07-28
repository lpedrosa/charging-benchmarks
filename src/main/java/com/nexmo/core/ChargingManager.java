package com.nexmo.core;

import static com.nexmo.core.Context.ContextStats;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ChargingManager {

    private final ContextRepository repository;
    private final ScheduledExecutorService pool;

    public ChargingManager(final ContextRepository repository) {
        this.repository = repository;
        this.pool = null;
        //this.pool = Executors.newScheduledThreadPool(10);
        // start ChargingTaskHere
    }

    public ContextStats start(String sessionId, String connectionId) {
        return charge(sessionId, connectionId, ChargingInfo.charge(6));
    }

    public ContextStats stop(String sessionId, String connectionId) {
        return charge(sessionId, connectionId, ChargingInfo.finalCharge(1));
    }

    private ContextStats charge(final String sessionId,
                        final String connectionId,
                        final ChargingInfo chargingInfo) {
        Context ctx = this.repository.get(sessionId, connectionId);

        if (ctx != null) {
            System.out.println("Charging context with [sessionId: "+sessionId+" connectionId: "+connectionId+"]");
            ContextStats stats = ctx.update(chargingInfo);
            System.out.println(stats);
            return stats;
        } 

        System.out.println("Failed to fetch context with [sessionId: "+sessionId+" connectionId: "+connectionId+"]");
        return null;
    }
}
