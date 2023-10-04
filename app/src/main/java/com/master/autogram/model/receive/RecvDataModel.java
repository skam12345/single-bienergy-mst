package com.master.autogram.model.receive;

public class RecvDataModel {
    private int sendNo;
    private String chargerId;
    private String plugNumber;
    private int charging;
    public RecvDataModel() {

    }

    public RecvDataModel(int sendNo, String chargerId, String plugNumber, int charging) {
        this.sendNo = sendNo;
        this.chargerId = chargerId;
        this.plugNumber = plugNumber;
        this.charging = charging;
    }

    public void setSendNo(int sendNo) {
        this.sendNo = sendNo;
    }

    
    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }
    
    public void setPlugNumber(String plugNumber) {
        this.plugNumber = plugNumber;
    }
    
    public void setCharging(int charging) {
        this.charging = charging;
    }
    
    public int getSendNo() {
        return sendNo;
    }
    
    public String getChargerId() {
        return chargerId;
    }

    public String getPlugNumber() {
        return plugNumber;
    }

    public int getCharging() {
        return charging;
    }
}