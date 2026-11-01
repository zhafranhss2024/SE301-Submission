package org.example.model;


public class ThresholdRange {
    private final double min;
    private final double max;

    public ThresholdRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isAboveMax(double value) {
        return value > max;
    }

    public boolean isBelowMin(double value) {
        return value < min;
    }

    public boolean isWithinRange(double value) {
        return value >= min && value <= max;
    }
}
