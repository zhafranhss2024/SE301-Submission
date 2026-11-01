package org.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class EventLogger {
    private final List<String> sensorLogs;

    public EventLogger() {
        this.sensorLogs = Collections.synchronizedList(new ArrayList<>());
    }

    public void logSensor(String message) {
        sensorLogs.add(new Date() + " : " + message);
    }

    public void logAlert(String chamber, String crop, String message) {
        System.out.println("[" + chamber + "][" + crop + "][ALERT] " + message);
    }

    public void logControl(String chamber, String message) {
        System.out.println("\t\u001B[32m[" + chamber + "][CONTROL] " + message + "\u001B[0m");
    }

    public List<String> getSensorLogsSnapshot() {
        synchronized (sensorLogs) {
            return new ArrayList<>(sensorLogs);
        }
    }

    public void showLogs() {
        for (String s : getSensorLogsSnapshot()) {
            System.out.println(s);
        }
    }
}
