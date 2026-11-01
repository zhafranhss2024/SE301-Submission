package org.example.control;

import org.example.model.ChamberState;


public interface ControlDevice {
    
    String getActionName();

    String applyAdjustment(ChamberState state);
}
