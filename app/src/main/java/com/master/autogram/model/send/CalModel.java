package com.master.autogram.model.send;

public class CalModel {
    private int ampare;
    private int voltage;
    private int percent;
    public CalModel() {
        
    }

    public void setAmpare(int ampare) {
        this.ampare = ampare;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getAmpare() {
        return ampare;
    }

    public int getVoltage() {
        return voltage;
    }

    public int getPercent() {
        return percent;
    }

}