package com.nexmo.core;

import static com.nexmo.core.Context.Type;

import java.util.Collection;

public interface ContextRepository {
    Collection<Context> getAll(Type type);
    Context get(String sessionId, String connectionId);
    void put(String sessionId, String connectionId, Context ctx);
    void removeAll(String sessionId);
}
