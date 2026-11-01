package org.example.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public final class LockFactory {
    public static final String ALERT_LOCK = "ALERT_LOCK";
    public static final String REPORT_LOCK = "REPORT_LOCK";
    public static final String RESOURCE_LOCK = "RESOURCE_LOCK";

    private static final LockFactory INSTANCE = new LockFactory();
    private final Map<String, Lock> locks = new ConcurrentHashMap<>();

    private LockFactory() {
        // Private constructor enforcing Singleton pattern
    }

    public static LockFactory getInstance() {
        return INSTANCE;
    }

    public static Lock getLock(String lockName) {
        return INSTANCE.locks.computeIfAbsent(lockName, key -> new ReentrantLock());
    }
}
