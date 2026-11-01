package org.example.sensor;

import org.example.model.ChamberState;
import org.example.model.CropProfile;


public interface SensorProcessor {
    
    void processReading(String chamberId, String cropName, double value, ChamberState state, CropProfile profile);
}
