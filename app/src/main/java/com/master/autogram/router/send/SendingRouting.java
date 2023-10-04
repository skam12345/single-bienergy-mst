package com.master.autogram.router.send;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.master.autogram.calculate.parameter.ControlCombine;
import com.master.autogram.calculate.parameter.QrExplodeCombine;
import com.master.autogram.calculate.parameter.QrRecognizeCombine;
import com.master.autogram.controller.DBLoopController;
import com.master.autogram.controller.SendRecvController;
import com.master.autogram.controller.SocketCommunicationController;
import com.master.autogram.model.receive.RecvDataModel;
import com.master.autogram.router.receive.RecvRouting;
import com.master.autogram.utils.constant.ChargerEVCConstant;
import com.master.autogram.utils.db.MysqlConnector;

public class SendingRouting {
    private static SocketCommunicationController socketController;
    private MysqlConnector connector;
    private RecvRouting recv;
    private ArrayList<HashMap<String, Object>> runList;
    private SendRecvController recvs;
    private boolean isExist;
    private Scanner scanner;
    public SendingRouting() {
        connector = new MysqlConnector();
        socketController = new SocketCommunicationController(connector);
        recv = new RecvRouting();
        isExist = false;
        scanner = new Scanner(System.in);
        recvs = new SendRecvController(connector);
    }

    public void routing() {
        running();
        // mainRunning();
    }

    public void mainRunning() {
        DBLoopController dbCon = new DBLoopController(connector);
        System.out.println("로그인을 시작합니다.");
        socketController.socketCertification(dbCon.callChargerId());
        while(!socketController.loginResult) {
            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(() -> {
            System.out.println("명령이 들어올 때까지 대기합니다.");
            while(!isExist) {
                try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
                runList = dbCon.callList();
                if(runList.size() != 0) {
                    isExist = true;
                }else {
                    isExist = false;
                }
            }
            System.out.println("명령을 실행합니다.");
            for(int i = 0; i < runList.size(); i++) {
                if(runList.get(i).get("code").equals("0AH02")) {
                    String id = recvs.currentChargerId((int)runList.get(i).get("keyNumber"));
                    ArrayList<String> chargerId = dbCon.callChargerId();
                    for(int j = 0; j < chargerId.size(); j++) {
                        String values = "";
                        for(int k = 0; k < socketController.values.get(j).length; k++) {
                            values += String.format(ChargerEVCConstant.FORMAT, socketController.values.get(j)[k]);
                        }
                        if(values.contains(id)) {
                            chargingControl(j, runList.get(i).get("code").toString(), runList.get(i).get("keyNumber").toString(), runList.get(i));
                        }
                    }
                    
                }else if(runList.get(i).get("code").equals("0AH03")) {
                    String id = recvs.currentChargerId((int)runList.get(i).get("keyNumber"));
                    ArrayList<String> chargerId = dbCon.callChargerId();
                    for(int j = 0; j < chargerId.size(); j++) {
                        String values = "";
                        for(int k = 0; k < socketController.values.get(j).length; k++) {
                            values += String.format(ChargerEVCConstant.FORMAT, socketController.values.get(j)[k]);
                        }
                        if(values.contains(id)) {
                            chargingControl(j, runList.get(i).get("code").toString(), runList.get(i).get("keyNumber").toString(), runList.get(i));
                        }
                    }
                }
            }
        });
    }

    public void chargingControl(int index, String code, String key, HashMap<String, Object> param) {
        RecvDataModel model = recv.receivedData(code, key).get(index);
        byte [] startHexArray = new ControlCombine(socketController.values.get(index), model.getPlugNumber(), model.getCharging()).startParameterCombine();
        byte [] stopHexArray = new ControlCombine(socketController.values.get(index), model.getPlugNumber(), model.getCharging()).stopParameterCombine();
        socketController.socketChargingStart(startHexArray, stopHexArray, model.getSendNo(), code, index, model.getPlugNumber(), param);
    }

    public void running() {
        System.out.println("어떤걸 수행하시겠습니까?  :: 1. QR 등록 | 2. QR 노출 | 3. 메인 | other. 메인");
        int result = scanner.nextInt();
        DBLoopController dbCon = new DBLoopController(connector);
        ArrayList<String> chargerId = dbCon.callChargerId();
        switch(result) {
            case 1:
                System.out.println("로그인을 시작합니다.");
                socketController.socketCertification(chargerId);
                while(!socketController.loginResult) {
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                byte [] qrHexDataArray = new QrRecognizeCombine(socketController.values.get(0)).QrParameterCombine("23032497990011012301");
                socketController.qrRecognize(qrHexDataArray);
                break;
            case 2:
                System.out.println("로그인을 시작합니다.");
                socketController.socketCertification(chargerId);
                while(!socketController.loginResult) {
                    try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                byte [] qrExplodeArray = new QrExplodeCombine(socketController.values.get(0)).QrExplodeParameterCombine();
                socketController.qrExplode(qrExplodeArray);
                break;
            default:
                mainRunning();
                break;
        }
    }
}
