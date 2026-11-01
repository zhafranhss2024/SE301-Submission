package org.example;

import org.example.control.ControlService;
import org.example.control.ControlWorker;
import org.example.crop.CropRegistry;
import org.example.model.CropProfile;
import org.example.sensor.ChamberSensorTask;
import org.example.sensor.SensorService;
import org.example.service.AlertManager;
import org.example.service.ChamberManager;
import org.example.service.EventLogger;
import org.example.service.ReportGenerator;
import org.example.service.ResourceMonitor;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class SVFRMS {
    private static final int DEFAULT_CONTROL_QUEUE_CAPACITY = 5;
    private static final int CHAMBER_THREAD_POOL_SIZE = 4;

    private final CropRegistry cropRegistry;
    private final EventLogger eventLogger;
    private final AlertManager alertManager;
    private final ChamberManager chamberManager;
    private final ResourceMonitor resourceMonitor;
    private final ControlService controlService;
    private final SensorService sensorService;
    private final ReportGenerator reportGenerator;

    public SVFRMS() {
        this.cropRegistry = new CropRegistry();
        this.eventLogger = new EventLogger();
        this.alertManager = new AlertManager(eventLogger);
        this.chamberManager = new ChamberManager();
        this.resourceMonitor = new ResourceMonitor();
        this.controlService = new ControlService(
                DEFAULT_CONTROL_QUEUE_CAPACITY, chamberManager, eventLogger);
        this.sensorService = new SensorService(
                cropRegistry, chamberManager, eventLogger, alertManager, controlService, resourceMonitor);
        this.reportGenerator = new ReportGenerator(
                alertManager, chamberManager, resourceMonitor);
    }

    public void processSensor(String chamber, String crop, String sensor, double value) {
        sensorService.processSensor(chamber, crop, sensor, value);
    }

    public void addControlRequest(String chamber, String control) {
        controlService.addControlRequest(chamber, control);
    }

    public void activateControl(String chamber, String control) {
        controlService.activateControl(chamber, control);
    }

    public void irrigationCycle() {
        resourceMonitor.irrigationCycle();
    }

    public void feedingCycle() {
        resourceMonitor.feedingCycle();
    }

    public void generateDailyReport() {
        reportGenerator.generateDailyReport();
    }

    public void archiveAlerts() {
        reportGenerator.archiveAlerts();
    }

    public void saveArchiveReport() {
        reportGenerator.saveArchiveReport();
    }

    public void showAlerts() {
        alertManager.showAlerts();
    }

    public void showLogs() {
        eventLogger.showLogs();
    }

    public void registerCrop(CropProfile profile) {
        cropRegistry.registerCrop(profile);
    }

    public boolean removeCrop(String cropName) {
        return cropRegistry.removeCrop(cropName);
    }

    
    public void runSimulation() throws InterruptedException {
        Thread activateControlThread = new Thread(new ControlWorker(controlService), "Control-Thread");
        activateControlThread.start();

        ExecutorService chamberPool = Executors.newFixedThreadPool(CHAMBER_THREAD_POOL_SIZE);
        try {
            List<Future<?>> tasks = List.of(
                    chamberPool.submit(new ChamberSensorTask("CH1", "Tomato", sensorService, resourceMonitor)),
                    chamberPool.submit(new ChamberSensorTask("CH2", "Strawberry", sensorService, resourceMonitor)),
                    chamberPool.submit(new ChamberSensorTask("CH3", "Lettuce", sensorService, resourceMonitor)),
                    chamberPool.submit(new ChamberSensorTask("CH4", "Chili", sensorService, resourceMonitor)));

            chamberPool.shutdown();
            if (!chamberPool.awaitTermination(1, TimeUnit.MINUTES)) {
                throw new IllegalStateException("Sensor tasks did not finish within one minute");
            }
            for (Future<?> task : tasks) {
                task.get();
            }
        } catch (ExecutionException e) {
            throw new IllegalStateException("Sensor task failed", e.getCause());
        } finally {
            chamberPool.shutdownNow();
            controlService.setRunning(false);
            activateControlThread.join();
        }

        Thread genReport = new Thread(this::generateDailyReport, "Report-Thread");
        Thread archiveAlerts = new Thread(this::archiveAlerts, "Archive-Thread");

        archiveAlerts.start();
        genReport.start();

        genReport.join();
        archiveAlerts.join();
        System.out.println("System finished sensor processing.");
        saveArchiveReport();
    }

    public static void main(String[] args) throws Exception {
        SVFRMS system = new SVFRMS();
        system.runSimulation();
    }
}
