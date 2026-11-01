package org.example.service;

import org.example.model.ChamberState;
import org.example.model.ChamberSummary;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ChamberManager {
    private final Map<String, ChamberState> chamberStates;
    private final Map<String, ChamberSummary> chamberSummaries;

    public ChamberManager() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    public ChamberManager(Map<String, ChamberState> statesMap, Map<String, ChamberSummary> summariesMap) {
        this.chamberStates = statesMap;
        this.chamberSummaries = summariesMap;
    }

    public ChamberState getChamberState(String chamberId) {
        return chamberStates.computeIfAbsent(chamberId, key -> new ChamberState());
    }

    public ChamberSummary getChamberSummary(String chamberId) {
        return chamberSummaries.computeIfAbsent(chamberId, key -> new ChamberSummary());
    }

    public void trackAlert(String chamberId, String alertMessage) {
        getChamberSummary(chamberId).recordAlert(alertMessage);
    }

    public void trackAction(String chamberId, String actionMessage) {
        getChamberSummary(chamberId).recordAction(actionMessage);
    }

    public Map<String, ChamberSummary> getChamberSummariesSnapshot() {
        return new LinkedHashMap<>(chamberSummaries);
    }
}
