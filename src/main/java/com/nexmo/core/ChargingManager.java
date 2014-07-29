package com.nexmo.core;

import static com.nexmo.core.Context.ContextStats;
import static com.nexmo.core.Context.Type;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChargingManager {
    private static final boolean NOT_DELTA = false;

    private final ContextRepository repository;
    private final ScheduledExecutorService pool;

    public ChargingManager(final ContextRepository repository) {
        this.repository = repository;
        this.pool = Executors.newScheduledThreadPool(10);
    }

    public void init() {
        System.out.println("---- Starting update charging task ----");
        final Runnable ttsPulseChargeTask = new PulseChargeTask(this.repository, Type.TTS);
        pool.scheduleAtFixedRate(ttsPulseChargeTask, 0, 1, TimeUnit.SECONDS);
        System.out.println("Task is now sweeping the context repo...");
    }

    public void shutdown(long timeout, TimeUnit timeUnit) {
        System.out.println("---- Shutting down the charge manager ----");
        try {
            this.pool.shutdown();
            boolean sucessfullShutdown = this.pool.awaitTermination(timeout, timeUnit);
            if (sucessfullShutdown) {
                System.out.println("Shutdown successfully!");
            } else {
                System.out.println("Failed to shutdown successfully!");
            }
        } catch (InterruptedException ex) {
            System.out.println("Something went terribly wrong...");
        }
    }

    public ContextStats start(String sessionId, String connectionId) {
        return charge(sessionId, connectionId, ChargingInfo.charge(6));
    }

    public ContextStats stop(String sessionId, String connectionId) {
        return charge(sessionId, connectionId, ChargingInfo.finalCharge(0));
    }

    private ContextStats charge(final String sessionId,
                        final String connectionId,
                        final ChargingInfo chargingInfo) {
        Context ctx = this.repository.get(sessionId, connectionId);

        if (ctx != null) {
            System.out.println("Charging context with [sessionId: "+sessionId+" connectionId: "+connectionId+"]");
            ContextStats stats = ctx.update(chargingInfo, NOT_DELTA);
            System.out.println(stats);
            return stats;
        } 

        System.out.println("Failed to fetch context with [sessionId: "+sessionId+" connectionId: "+connectionId+"]");
        return null;
    }
}
