package org.example.service;

import org.example.concurrency.LockFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;


public class AlertManager {
    private final List<String> activeAlerts = new ArrayList<>();
    private final List<String> archivedAlerts = new ArrayList<>();
    private int totalAlerts = 0;
    private int totalAlertsGenerated = 0;
    private final Lock alertLock = LockFactory.getLock(LockFactory.ALERT_LOCK);
    private final EventLogger eventLogger;

    public AlertManager(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    public void createAlert(String chamber, String crop, String message) {
        alertLock.lock();
        try {
            activeAlerts.add(message);
            totalAlerts++;
            totalAlertsGenerated++;
        } finally {
            alertLock.unlock();
        }
        eventLogger.logAlert(chamber, crop, message);
    }

    public int archiveActiveAlerts() {
        alertLock.lock();
        try {
            if (!activeAlerts.isEmpty()) {
                int count = activeAlerts.size();
                archivedAlerts.addAll(activeAlerts);
                activeAlerts.clear();
                totalAlerts = 0;
                return count;
            }
            return 0;
        } finally {
            alertLock.unlock();
        }
    }

    public List<String> getActiveAlertsSnapshot() {
        alertLock.lock();
        try {
            return new ArrayList<>(activeAlerts);
        } finally {
            alertLock.unlock();
        }
    }

    public List<String> getArchivedAlertsSnapshot() {
        alertLock.lock();
        try {
            return new ArrayList<>(archivedAlerts);
        } finally {
            alertLock.unlock();
        }
    }

    public int getTotalActiveAlerts() {
        alertLock.lock();
        try {
            return totalAlerts;
        } finally {
            alertLock.unlock();
        }
    }

    public int getTotalAlertsGenerated() {
        alertLock.lock();
        try {
            return totalAlertsGenerated;
        } finally {
            alertLock.unlock();
        }
    }

    public void showAlerts() {
        for (String a : getActiveAlertsSnapshot()) {
            System.out.println(a);
        }
    }
}
