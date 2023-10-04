package com.master.autogram.model.send;

public class SendControlModel {
    private int ampare;
    private int voltage;
    private int percent;
    public SendControlModel() {
        
    }

    public void setCinCode(int ampare) {
        this.ampare = ampare;
    }

    public void setChnNo(int voltage) {
        this.voltage = voltage;
    }

    public void setPlugNumber(int percent) {
        this.percent = percent;
    }

    public int getCinCode() {
        return ampare;
    }

    public int getChnNo() {
        return voltage;
    }

    public int getPlugNumber() {
        return percent;
    }
}