package org.example.model;


public class ControlRequest {
    private final String chamberId;
    private final String controlName;

    public ControlRequest(String chamberId, String controlName) {
        this.chamberId = chamberId;
        this.controlName = controlName;
    }

    public String getChamberId() {
        return chamberId;
    }

    public String getControlName() {
        return controlName;
    }

    @Override
    public String toString() {
        return chamberId + "|" + controlName;
    }
}
