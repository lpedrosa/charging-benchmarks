package com.nexmo.core;

import static com.nexmo.core.Context.ContextStats;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ChargingManager {

    private final ContextRepository repository;
    private final ScheduledExecutorService pool;

    public ChargingManager(final ContextRepository repository) {
        this.repository = repository;
        this.pool = Executors.newScheduledThreadPool(10);
        // start ChargingTaskHere
    }

    public void start(String sessionId, String connectionId) {
        charge(sessionId, connectionId, ChargingInfo.charge(6));
    }

    public void stop(String sessionId, String connectionId) {
        charge(sessionId, connectionId, ChargingInfo.finalCharge(1));
    }

    private void charge(final String sessionId,
                        final String connectionId,
                        final ChargingInfo chargingInfo) {
        Context ctx = this.repository.get(sessionId, connectionId);

        if (ctx != null) {
            System.out.printf("Charging context with [sessionId:%s connectionId:%s]%\n", sessionId, connectionId);
            ContextStats stats = ctx.update(chargingInfo);
            System.out.println(stats);
        } else {
            System.out.printf("Failed to fetch context with [sessionId:%s connectionId:%s]%\n", sessionId, connectionId);
        }
    }
}
