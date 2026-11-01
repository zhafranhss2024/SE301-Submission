package org.example.sensor;

import org.example.service.ResourceMonitor;
import java.util.Random;


public class ChamberSensorTask implements Runnable {
    private final String chamber;
    private final String crop;
    private final SensorService sensorService;
    private final ResourceMonitor resourceMonitor;
    private final Random random = new Random();

    public ChamberSensorTask(
            String chamber,
            String crop,
            SensorService sensorService,
            ResourceMonitor resourceMonitor) {
        this.chamber = chamber;
        this.crop = crop;
        this.sensorService = sensorService;
        this.resourceMonitor = resourceMonitor;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            sensorService.processSensor(chamber, crop, SensorService.TEMPERATURE, 10 + random.nextInt(30));
            sensorService.processSensor(chamber, crop, SensorService.HUMIDITY, 40 + random.nextInt(60));
            sensorService.processSensor(chamber, crop, SensorService.SOIL, 20 + random.nextInt(60));
            sensorService.processSensor(chamber, crop, SensorService.LIGHT, 300 + random.nextInt(900));
            sensorService.processSensor(chamber, crop, SensorService.NUTRIENT, 20 + random.nextInt(80));
            resourceMonitor.addWaterUsage(2);
            resourceMonitor.addEnergyUsage(1);
        }
    }
}
