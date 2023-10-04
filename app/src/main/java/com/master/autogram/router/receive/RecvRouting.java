package com.master.autogram.router.receive;

import java.util.ArrayList;

import com.master.autogram.controller.SendRecvController;
import com.master.autogram.model.receive.RecvDataModel;
import com.master.autogram.utils.db.MysqlConnector;

public class RecvRouting {
    private MysqlConnector connector;
    SendRecvController controller;
    public RecvRouting() {
        connector = new MysqlConnector();
        controller = new SendRecvController(connector);
    }

    public ArrayList<RecvDataModel> receivedData(String code, String key) {
        ArrayList<RecvDataModel> data = null;
        if(code.equals("0AH02")) {
            data = controller.callChargerInfo(key, code);
        }else if(code.equals("0AH03")) {
            data = controller.callChargerInfo(key, code);
        }else if(code.equals("01H01")) {
            data = controller.callChargerInfo(key, code);
        }
        return data;
    }
} 