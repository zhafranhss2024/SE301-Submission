package org.example.service;

import org.example.concurrency.LockFactory;
import java.util.concurrent.locks.Lock;


public class ResourceMonitor {
    private int totalWaterUsage = 0;
    private int totalEnergyUsage = 0;
    private final Lock resourceLock = LockFactory.getLock(LockFactory.RESOURCE_LOCK);

    public void addWaterUsage(int liters) {
        resourceLock.lock();
        try {
            totalWaterUsage += liters;
        } finally {
            resourceLock.unlock();
        }
    }

    public void addEnergyUsage(int kwh) {
        resourceLock.lock();
        try {
            totalEnergyUsage += kwh;
        } finally {
            resourceLock.unlock();
        }
    }

    public void irrigationCycle() {
        addWaterUsage(100);
    }

    public void feedingCycle() {
        addEnergyUsage(20);
    }

    public int getTotalWaterUsage() {
        resourceLock.lock();
        try {
            return totalWaterUsage;
        } finally {
            resourceLock.unlock();
        }
    }

    public int getTotalEnergyUsage() {
        resourceLock.lock();
        try {
            return totalEnergyUsage;
        } finally {
            resourceLock.unlock();
        }
    }
}
