package org.example.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ChamberSummary {
    private int alerts = 0;
    private int mitigationActions = 0;
    private String lastAction = "No action";
    private String lastAlert = "None";

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public void recordAlert(String alertMessage) {
        writeLock.lock();
        try {
            this.alerts++;
            this.lastAlert = alertMessage;
        } finally {
            writeLock.unlock();
        }
    }

    public void recordAction(String actionMessage) {
        writeLock.lock();
        try {
            this.mitigationActions++;
            this.lastAction = actionMessage;
        } finally {
            writeLock.unlock();
        }
    }

    public int getAlerts() {
        readLock.lock();
        try {
            return alerts;
        } finally {
            readLock.unlock();
        }
    }

    public int getMitigationActions() {
        readLock.lock();
        try {
            return mitigationActions;
        } finally {
            readLock.unlock();
        }
    }

    public String getLastAction() {
        readLock.lock();
        try {
            return lastAction;
        } finally {
            readLock.unlock();
        }
    }

    public String getLastAlert() {
        readLock.lock();
        try {
            return lastAlert;
        } finally {
            readLock.unlock();
        }
    }

    public String formatSummaryLine(String chamberId) {
        readLock.lock();
        try {
            return chamberId + " -> Alerts=" + alerts
                    + ", Actions=" + mitigationActions
                    + ", Last Action=" + lastAction
                    + ", Last Alert=" + lastAlert;
        } finally {
            readLock.unlock();
        }
    }
}
