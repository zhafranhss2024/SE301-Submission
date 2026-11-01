package org.example.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ChamberState {
    private double temperature = 24;
    private double humidity = 70;
    private double soilMoisture = 60;
    private double lightLevel = 600;
    private double nutrientLevel = 70;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public double getTemperature() {
        readLock.lock();
        try {
            return temperature;
        } finally {
            readLock.unlock();
        }
    }

    public void setTemperature(double temperature) {
        writeLock.lock();
        try {
            this.temperature = temperature;
        } finally {
            writeLock.unlock();
        }
    }

    public double adjustTemperature(double delta, double minBound, double maxBound) {
        writeLock.lock();
        try {
            this.temperature = Math.min(maxBound, Math.max(minBound, this.temperature + delta));
            return this.temperature;
        } finally {
            writeLock.unlock();
        }
    }

    public double getHumidity() {
        readLock.lock();
        try {
            return humidity;
        } finally {
            readLock.unlock();
        }
    }

    public void setHumidity(double humidity) {
        writeLock.lock();
        try {
            this.humidity = humidity;
        } finally {
            writeLock.unlock();
        }
    }

    public double adjustHumidity(double delta, double minBound, double maxBound) {
        writeLock.lock();
        try {
            this.humidity = Math.min(maxBound, Math.max(minBound, this.humidity + delta));
            return this.humidity;
        } finally {
            writeLock.unlock();
        }
    }

    public double getSoilMoisture() {
        readLock.lock();
        try {
            return soilMoisture;
        } finally {
            readLock.unlock();
        }
    }

    public void setSoilMoisture(double soilMoisture) {
        writeLock.lock();
        try {
            this.soilMoisture = soilMoisture;
        } finally {
            writeLock.unlock();
        }
    }

    public double adjustSoilMoisture(double delta, double minBound, double maxBound) {
        writeLock.lock();
        try {
            this.soilMoisture = Math.min(maxBound, Math.max(minBound, this.soilMoisture + delta));
            return this.soilMoisture;
        } finally {
            writeLock.unlock();
        }
    }

    public double getLightLevel() {
        readLock.lock();
        try {
            return lightLevel;
        } finally {
            readLock.unlock();
        }
    }

    public void setLightLevel(double lightLevel) {
        writeLock.lock();
        try {
            this.lightLevel = lightLevel;
        } finally {
            writeLock.unlock();
        }
    }

    public double adjustLightLevel(double delta, double minBound, double maxBound) {
        writeLock.lock();
        try {
            this.lightLevel = Math.min(maxBound, Math.max(minBound, this.lightLevel + delta));
            return this.lightLevel;
        } finally {
            writeLock.unlock();
        }
    }

    public double getNutrientLevel() {
        readLock.lock();
        try {
            return nutrientLevel;
        } finally {
            readLock.unlock();
        }
    }

    public void setNutrientLevel(double nutrientLevel) {
        writeLock.lock();
        try {
            this.nutrientLevel = nutrientLevel;
        } finally {
            writeLock.unlock();
        }
    }

    public double adjustNutrientLevel(double delta, double minBound, double maxBound) {
        writeLock.lock();
        try {
            this.nutrientLevel = Math.min(maxBound, Math.max(minBound, this.nutrientLevel + delta));
            return this.nutrientLevel;
        } finally {
            writeLock.unlock();
        }
    }
}
