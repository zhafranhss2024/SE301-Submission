package org.example.control;

import org.example.model.ControlRequest;


public class ControlWorker implements Runnable {
    private final ControlService controlService;

    public ControlWorker(ControlService controlService) {
        this.controlService = controlService;
    }

    @Override
    public void run() {
        while (true) {
            ControlRequest request = controlService.pollNextRequest();
            if (request == null) {
                break;
            }
            controlService.activateControl(request.getChamberId(), request.getControlName());
        }
    }
}
