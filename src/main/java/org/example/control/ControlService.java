package org.example.control;

import org.example.model.ChamberState;
import org.example.model.ControlRequest;
import org.example.service.ChamberManager;
import org.example.service.EventLogger;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;


public class ControlService {
    public static final String COOLING_FAN = "Cooling Fan";
    public static final String WATER_PUMP = "Water Pump";
    public static final String NUTRIENT_INJECTOR = "Nutrient Injector";
    public static final String HUMIDIFIER = "Humidifier";
    public static final String DEHUMIDIFIER = "Dehumidifier";
    public static final String GROW_LIGHT = "Grow Light";

    private final Map<String, ControlDevice> devices = new ConcurrentHashMap<>();
    private final Queue<ControlRequest> queue = new LinkedList<>();
    private final int maxCapacity;
    private final ChamberManager chamberManager;
    private final EventLogger eventLogger;
    private volatile boolean running = true;

    public ControlService(
            int maxCapacity,
            ChamberManager chamberManager,
            EventLogger eventLogger) {
        this.maxCapacity = maxCapacity;
        this.chamberManager = chamberManager;
        this.eventLogger = eventLogger;
        registerDefaultDevices();
    }

    private void registerDefaultDevices() {
        registerDevice(COOLING_FAN, new ControlDevice() {
            @Override
            public String getActionName() {
                return "Cooling Fan Activated";
            }

            @Override
            public String applyAdjustment(ChamberState state) {
                double newTemp = state.adjustTemperature(-5, 18, Double.MAX_VALUE);
                return "Temperature reduced to " + newTemp + "C";
            }
        });

        registerDevice(WATER_PUMP, new ControlDevice() {
            @Override
            public String getActionName() {
                return "Water Pump Activated";
            }

            @Override
            public String applyAdjustment(ChamberState state) {
                double newMoisture = state.adjustSoilMoisture(20, -Double.MAX_VALUE, 100);
                return "Soil moisture increased to " + newMoisture + "%";
            }
        });

        registerDevice(NUTRIENT_INJECTOR, new ControlDevice() {
            @Override
            public String getActionName() {
                return "Nutrient Injector Activated";
            }

            @Override
            public String applyAdjustment(ChamberState state) {
                double newNutrient = state.adjustNutrientLevel(15, -Double.MAX_VALUE, 100);
                return "Nutrient level increased to " + newNutrient + "%";
            }
        });

        registerDevice(HUMIDIFIER, new ControlDevice() {
            @Override
            public String getActionName() {
                return "Humidifier Activated";
            }

            @Override
            public String applyAdjustment(ChamberState state) {
                double newHumidity = state.adjustHumidity(10, -Double.MAX_VALUE, 100);
                return "Humidity increased to " + newHumidity + "%";
            }
        });

        registerDevice(DEHUMIDIFIER, new ControlDevice() {
            @Override
            public String getActionName() {
                return "Dehumidifier Activated";
            }

            @Override
            public String applyAdjustment(ChamberState state) {
                double newHumidity = state.adjustHumidity(-10, 30, Double.MAX_VALUE);
                return "Humidity reduced to " + newHumidity + "%";
            }
        });

        registerDevice(GROW_LIGHT, new ControlDevice() {
            @Override
            public String getActionName() {
                return "Grow Light Activated";
            }

            @Override
            public String applyAdjustment(ChamberState state) {
                double newLight = state.adjustLightLevel(150, -Double.MAX_VALUE, 1000);
                return "Light level increased to " + newLight + " lux";
            }
        });
    }

    public void registerDevice(String deviceName, ControlDevice device) {
        if (deviceName != null && device != null) {
            devices.put(deviceName, device);
        }
    }

    public boolean removeDevice(String deviceName) {
        if (deviceName == null) {
            return false;
        }
        return devices.remove(deviceName) != null;
    }

    public void addControlRequest(String chamberId, String controlName) {
        synchronized (queue) {
            while (running && queue.size() >= maxCapacity) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (!running) {
                return;
            }
            queue.add(new ControlRequest(chamberId, controlName));
            queue.notifyAll();
        }
    }

    public ControlRequest pollNextRequest() {
        synchronized (queue) {
            while (queue.isEmpty() && running) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            if (queue.isEmpty() && !running) {
                return null;
            }
            ControlRequest request = queue.remove();
            queue.notifyAll();
            return request;
        }
    }

    public void activateControl(String chamberId, String controlName) {
        ChamberState state = chamberManager.getChamberState(chamberId);
        ControlDevice device = devices.get(controlName);
        if (device != null) {
            String detailMessage = device.applyAdjustment(state);
            String actionName = device.getActionName();
            chamberManager.trackAction(chamberId, actionName);
            eventLogger.logControl(chamberId, actionName);
            eventLogger.logControl(chamberId, detailMessage);
        }
    }

    public void setRunning(boolean running) {
        synchronized (queue) {
            this.running = running;
            queue.notifyAll();
        }
    }
}
