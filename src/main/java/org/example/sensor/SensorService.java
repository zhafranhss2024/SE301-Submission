package org.example.sensor;

import org.example.control.ControlService;
import org.example.crop.CropRegistry;
import org.example.model.ChamberState;
import org.example.model.CropProfile;
import org.example.model.ThresholdRange;
import org.example.service.AlertManager;
import org.example.service.ChamberManager;
import org.example.service.EventLogger;
import org.example.service.ResourceMonitor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SensorService {
    public static final String TEMPERATURE = "Temperature";
    public static final String HUMIDITY = "Humidity";
    public static final String SOIL = "Soil";
    public static final String LIGHT = "Light";
    public static final String NUTRIENT = "Nutrient";

    private static final ThresholdRange DEFAULT_LIGHT_RANGE = new ThresholdRange(500, Double.MAX_VALUE);
    private static final ThresholdRange DEFAULT_NUTRIENT_RANGE = new ThresholdRange(40, Double.MAX_VALUE);

    private final Map<String, SensorProcessor> processors = new ConcurrentHashMap<>();
    private final CropRegistry cropRegistry;
    private final ChamberManager chamberManager;
    private final EventLogger eventLogger;
    private final AlertManager alertManager;
    private final ControlService controlService;
    private final ResourceMonitor resourceMonitor;

    public SensorService(
            CropRegistry cropRegistry,
            ChamberManager chamberManager,
            EventLogger eventLogger,
            AlertManager alertManager,
            ControlService controlService,
            ResourceMonitor resourceMonitor) {
        this.cropRegistry = cropRegistry;
        this.chamberManager = chamberManager;
        this.eventLogger = eventLogger;
        this.alertManager = alertManager;
        this.controlService = controlService;
        this.resourceMonitor = resourceMonitor;
        registerDefaultProcessors();
    }

    private void registerDefaultProcessors() {
        registerProcessor(TEMPERATURE, (chamber, crop, value, state, profile) -> {
            state.setTemperature(value);
            if (profile == null || profile.getTemperatureRange() == null) {
                return;
            }
            ThresholdRange range = profile.getTemperatureRange();
            if (range.isAboveMax(value)) {
                String msg = chamber + " " + crop + " High Temp";
                recordAlert(chamber, crop, msg);
                if (profile.getHighTempEnergyUsage() > 0) {
                    resourceMonitor.addEnergyUsage(profile.getHighTempEnergyUsage());
                }
                controlService.addControlRequest(chamber, ControlService.COOLING_FAN);
            }
            if (range.isBelowMin(value)) {
                String msg = chamber + " " + crop + " Low Temp";
                recordAlert(chamber, crop, msg);
                if (profile.getLowTempEnergyUsage() > 0) {
                    resourceMonitor.addEnergyUsage(profile.getLowTempEnergyUsage());
                }
            }
        });

        registerProcessor(HUMIDITY, (chamber, crop, value, state, profile) -> {
            state.setHumidity(value);
            if (profile == null || profile.getHumidityRange() == null) {
                return;
            }
            ThresholdRange range = profile.getHumidityRange();
            if (range.isAboveMax(value)) {
                String msg = chamber + " " + crop + " High Humidity";
                recordAlert(chamber, crop, msg);
                controlService.addControlRequest(chamber, ControlService.DEHUMIDIFIER);
            }
            if (range.isBelowMin(value)) {
                String msg = chamber + " " + crop + " Low Humidity";
                recordAlert(chamber, crop, msg);
                if (profile.getLowHumidityWaterUsage() > 0) {
                    resourceMonitor.addWaterUsage(profile.getLowHumidityWaterUsage());
                }
                controlService.addControlRequest(chamber, ControlService.HUMIDIFIER);
            }
        });

        registerProcessor(SOIL, (chamber, crop, value, state, profile) -> {
            state.setSoilMoisture(value);
            if (profile == null || profile.getSoilMoistureRange() == null) {
                return;
            }
            ThresholdRange range = profile.getSoilMoistureRange();
            if (range.isBelowMin(value)) {
                String msg = chamber + " " + crop + " Low Soil Moisture";
                recordAlert(chamber, crop, msg);
                if (profile.getLowSoilWaterUsage() > 0) {
                    resourceMonitor.addWaterUsage(profile.getLowSoilWaterUsage());
                }
                controlService.addControlRequest(chamber, ControlService.WATER_PUMP);
            }
        });

        registerProcessor(LIGHT, (chamber, crop, value, state, profile) -> {
            state.setLightLevel(value);
            ThresholdRange range = (profile != null && profile.getLightLevelRange() != null)
                    ? profile.getLightLevelRange()
                    : DEFAULT_LIGHT_RANGE;
            if (range.isBelowMin(value)) {
                String msg = chamber + " Low Light Level";
                recordAlert(chamber, crop, msg);
                controlService.addControlRequest(chamber, ControlService.GROW_LIGHT);
            }
        });

        registerProcessor(NUTRIENT, (chamber, crop, value, state, profile) -> {
            state.setNutrientLevel(value);
            ThresholdRange range = (profile != null && profile.getNutrientLevelRange() != null)
                    ? profile.getNutrientLevelRange()
                    : DEFAULT_NUTRIENT_RANGE;
            if (range.isBelowMin(value)) {
                String msg = chamber + " Low Nutrient Level";
                recordAlert(chamber, crop, msg);
                controlService.addControlRequest(chamber, ControlService.NUTRIENT_INJECTOR);
            }
        });
    }

    private void recordAlert(String chamber, String crop, String alertMessage) {
        chamberManager.trackAlert(chamber, alertMessage);
        alertManager.createAlert(chamber, crop, alertMessage);
    }

    public void registerProcessor(String sensorType, SensorProcessor processor) {
        if (sensorType != null && processor != null) {
            processors.put(sensorType, processor);
        }
    }

    public boolean removeProcessor(String sensorType) {
        if (sensorType == null) {
            return false;
        }
        return processors.remove(sensorType) != null;
    }

    public void processSensor(String chamber, String crop, String sensor, double value) {
        eventLogger.logSensor("Processing " + crop + " " + sensor + " = " + value);
        ChamberState state = chamberManager.getChamberState(chamber);
        CropProfile profile = cropRegistry.getCropProfile(crop);
        SensorProcessor processor = processors.get(sensor);
        if (processor != null) {
            processor.processReading(chamber, crop, value, state, profile);
        }
    }
}
