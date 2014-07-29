package com.nexmo.core;

import static com.nexmo.core.Context.ContextStats;
import static com.nexmo.core.Context.Type;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class ChargingManagerTest {

    private static String SESSION_ID = "SESSION";

    private ContextRepository repository;
    private ChargingManager chargingManager;

    @Before
    public void setUp() {
        this.repository = new MyContextRepository();
        createAndPut(SESSION_ID+"1", "1", Type.TTS);
        createAndPut(SESSION_ID+"2", "2", Type.TTS);
        createAndPut(SESSION_ID+"3", "3", Type.TTS);
        createAndPut(SESSION_ID+"4", "4", Type.TTS);

        createAndPut(SESSION_ID+"5", "5", Type.CALL);
        createAndPut(SESSION_ID+"5", "6", Type.CALL);

        this.chargingManager = new ChargingManager(this.repository);
    }
    
    @Test
    public void simpleStartAndStop() {
        ContextStats stats = chargingManager.start(SESSION_ID+"1", "1");
        assertNotNull(stats);
        assertEquals(6, stats.getTotalChargedUnits());
        stats = chargingManager.stop(SESSION_ID+"1","1");
    }

    @Test
    public void startSomeWaitABitAndStop() {
        this.chargingManager.init();

        ContextStats stats = chargingManager.start(SESSION_ID+"1", "1");
        assertNotNull(stats);
        assertEquals(6, stats.getTotalChargedUnits());
        stats = chargingManager.start(SESSION_ID+"2", "2");
        assertNotNull(stats);
        assertEquals(6, stats.getTotalChargedUnits());

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ex) {
            fail("Failed to wait 5 seconds!");
        }

        stats = chargingManager.stop(SESSION_ID+"1","1");
        assertNotNull(stats);
        stats = chargingManager.stop(SESSION_ID+"2","2");
        assertNotNull(stats);

        this.chargingManager.shutdown(2, TimeUnit.SECONDS);
    }

    private void createAndPut(String sessionId, String connectionId, Type type) {
        if (this.repository != null) {
            final Context ctx = new Context(sessionId, connectionId, type);
            this.repository.put(sessionId, connectionId, ctx);
        }
    }
}
