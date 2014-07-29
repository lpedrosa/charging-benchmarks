package com.nexmo.core;

import static com.nexmo.core.Context.ContextStats;
import static com.nexmo.core.Context.Type;

import java.lang.Runnable;
import java.util.Collection;

public class PulseChargeTask implements Runnable {

    private static final boolean IS_DELTA_UPDATE = true;

    private final ContextRepository repository;
    private final Type handledType;

    public PulseChargeTask(final ContextRepository repository, final Type handledType) {
        this.repository = repository;
        this.handledType = handledType;
    }

    @Override
    public void run() {
        final Collection<Context> allContextsOfType = this.repository.getAll(this.handledType);

        for (Context c : allContextsOfType) {
            ContextStats stats = c.update(ChargingInfo.charge(1), IS_DELTA_UPDATE);
            if (stats != null) {
                System.out.println(stats);
            } else {
                System.out.println("Didn't charge anything for context: " + c);
            }
        }
    }
}
