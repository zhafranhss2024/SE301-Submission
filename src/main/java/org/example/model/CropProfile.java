package org.example.model;


public class CropProfile {
    private final String name;
    private final ThresholdRange temperatureRange;
    private final ThresholdRange humidityRange;
    private final ThresholdRange soilMoistureRange;
    private final ThresholdRange lightLevelRange;
    private final ThresholdRange nutrientLevelRange;

    private final int highTempEnergyUsage;
    private final int lowTempEnergyUsage;
    private final int lowHumidityWaterUsage;
    private final int lowSoilWaterUsage;

    private CropProfile(Builder builder) {
        this.name = builder.name;
        this.temperatureRange = builder.temperatureRange;
        this.humidityRange = builder.humidityRange;
        this.soilMoistureRange = builder.soilMoistureRange;
        this.lightLevelRange = builder.lightLevelRange;
        this.nutrientLevelRange = builder.nutrientLevelRange;
        this.highTempEnergyUsage = builder.highTempEnergyUsage;
        this.lowTempEnergyUsage = builder.lowTempEnergyUsage;
        this.lowHumidityWaterUsage = builder.lowHumidityWaterUsage;
        this.lowSoilWaterUsage = builder.lowSoilWaterUsage;
    }

    public String getName() {
        return name;
    }

    public ThresholdRange getTemperatureRange() {
        return temperatureRange;
    }

    public ThresholdRange getHumidityRange() {
        return humidityRange;
    }

    public ThresholdRange getSoilMoistureRange() {
        return soilMoistureRange;
    }

    public ThresholdRange getLightLevelRange() {
        return lightLevelRange;
    }

    public ThresholdRange getNutrientLevelRange() {
        return nutrientLevelRange;
    }

    public int getHighTempEnergyUsage() {
        return highTempEnergyUsage;
    }

    public int getLowTempEnergyUsage() {
        return lowTempEnergyUsage;
    }

    public int getLowHumidityWaterUsage() {
        return lowHumidityWaterUsage;
    }

    public int getLowSoilWaterUsage() {
        return lowSoilWaterUsage;
    }

    
    public static class Builder {
        private final String name;
        private ThresholdRange temperatureRange;
        private ThresholdRange humidityRange;
        private ThresholdRange soilMoistureRange;
        private ThresholdRange lightLevelRange = new ThresholdRange(500, 1000);
        private ThresholdRange nutrientLevelRange = new ThresholdRange(40, 100);
        private int highTempEnergyUsage = 0;
        private int lowTempEnergyUsage = 0;
        private int lowHumidityWaterUsage = 0;
        private int lowSoilWaterUsage = 0;

        public Builder(String name) {
            this.name = name;
        }

        public Builder temperatureRange(double min, double max) {
            this.temperatureRange = new ThresholdRange(min, max);
            return this;
        }

        public Builder humidityRange(double min, double max) {
            this.humidityRange = new ThresholdRange(min, max);
            return this;
        }

        public Builder soilMoistureRange(double min, double max) {
            this.soilMoistureRange = new ThresholdRange(min, max);
            return this;
        }

        public Builder lightLevelRange(double min, double max) {
            this.lightLevelRange = new ThresholdRange(min, max);
            return this;
        }

        public Builder nutrientLevelRange(double min, double max) {
            this.nutrientLevelRange = new ThresholdRange(min, max);
            return this;
        }

        public Builder tempEnergyUsage(int highTempEnergy, int lowTempEnergy) {
            this.highTempEnergyUsage = highTempEnergy;
            this.lowTempEnergyUsage = lowTempEnergy;
            return this;
        }

        public Builder lowHumidityWaterUsage(int waterLiters) {
            this.lowHumidityWaterUsage = waterLiters;
            return this;
        }

        public Builder lowSoilWaterUsage(int waterLiters) {
            this.lowSoilWaterUsage = waterLiters;
            return this;
        }

        public CropProfile build() {
            return new CropProfile(this);
        }
    }
}
