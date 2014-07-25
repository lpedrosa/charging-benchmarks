package com.nexmo.core;

import static com.nexmo.core.Context.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

public class MyContextRepository implements ContextRepository {
    private final ConcurrentMap<String, ConcurrentMap<String, Context>> repository;

    public MyContextRepository() {
        this.repository = new ConcurrentHashMap<>();
    }

    @Override
    public Context get(String sessionId, String connectionId) {
        ConcurrentMap<String, Context> subMap = this.repository.get(sessionId);
        return subMap != null ? subMap.get(connectionId) : null;
    }

    @Override
    public Collection<Context> getAll(Type type) {
        Collection<ConcurrentMap<String, Context>> subMapValues = this.repository.values();
        
        Collection<Context> allContextsOffType = new ArrayList<>();
        for (ConcurrentMap<String, Context> concurrentMap : subMapValues) {
            for(Context ctx : concurrentMap.values()) {
                if (ctx.getType() == type) {
                    allContextsOffType.add(ctx);
                }
            }
        }
        return allContextsOffType;
    }

    @Override
    public void put(String sessionId, String connectionId, Context ctx) {
        ConcurrentMap<String, Context> subMap = this.repository.get(sessionId);

        if (subMap == null) {
            subMap = new ConcurrentHashMap<>();
        }

        subMap.put(connectionId, ctx);
        this.repository.put(sessionId, subMap);
    }

    @Override
    public void removeAll(String sessionId) {
        this.repository.remove(sessionId);
    }
}
