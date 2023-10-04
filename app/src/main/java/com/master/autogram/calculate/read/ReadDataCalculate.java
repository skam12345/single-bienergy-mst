package com.master.autogram.calculate.read;

import com.master.autogram.model.send.CalModel;

public class ReadDataCalculate {
    private CalModel model;
    public ReadDataCalculate(CalModel model) {
        this.model = model;
    }

    public int kwhCal() {
        int kwh = model.getAmpare() * model.getVoltage();
        return kwh;
    }
}