package com.master.autogram.controller;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.master.autogram.calculate.parameter.HeartBeatCombine;
import com.master.autogram.calculate.parameter.LoginCombine;
import com.master.autogram.calculate.parameter.ReadCurrentCombine;
import com.master.autogram.calculate.parameter.RecordCombine;
import com.master.autogram.calculate.read.ReadDataCalculate;
import com.master.autogram.model.send.CalModel;
import com.master.autogram.utils.constant.ChargerEVCConstant;
import com.master.autogram.utils.db.MysqlConnector;

public class SocketCommunicationController {
    public ServerSocket server;
    private static int port = 2020;
    private StringBuilder certification;
    private StringBuilder login;
    private StringBuilder start;
    private StringBuilder reading;
    public Socket socket;
    private MysqlConnector connector;
    private String ip;
    private RecordCombine combine;
    public boolean controlstart = false;
    public boolean loginResult = false;
    private byte[] heart;
    public ArrayList<byte[]> values;
    public int loginCount = 0;

    // 생성자
    public SocketCommunicationController(MysqlConnector connector) {
        try {
            server = new ServerSocket(port);
            socket = server.accept();
            socket.setSoTimeout(100000000);
            certification = new StringBuilder();
            login = new StringBuilder();
            start = new StringBuilder();
            reading = new StringBuilder();
            this.connector = connector;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 충전기와 연결 시 충전기에서 로그인하라고 지시 내리는 HEX코드를 받는 메서드
    public void socketCertification(ArrayList<String> chargerId) {
        ExecutorService threadService = Executors.newFixedThreadPool(1); // 스레딩 처리
        threadService.execute(() -> {
            try {
                InputStream in = socket.getInputStream(); // 충전기로부터 MSG 받는 객체
                DataInputStream dis = new DataInputStream(in); // 충전기로부터 데이터 받는 객체
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 받은 데이터 Byte 배열로 전환하는 객체
                byte[] dataBox = new byte[ChargerEVCConstant.SIZE]; // 최대크기 2048
                int read; // 읽은 데이터 길이
                byte[] bytes = null;
                while (true) {
                    ip = (((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/",
                            "");
                    if (ip != null) {
                        if ((read = dis.read(dataBox)) > -1) {
                            baos.write(dataBox, 0, read); // 바이트 최대 공간과 읽은 데이터 몇개씩 자를 지 ByteArrayOutputStream으로 전달하여
                                                          // byte 배열로 변환
                            bytes = baos.toByteArray();
                            for (byte b : bytes) { // 읽은 데이터 길이 중 2개씩 받아옴
                                certification.append(String.format(ChargerEVCConstant.FORMAT, b)); // 받은 데이터 16진수로 변환
                            }

                            if (certification.toString().contains("68") && certification.toString().contains("A1")
                                    && certification.toString().contains("16")) { // 로그인하라는 지시 데이터 조건 체크
                                values = new ArrayList<byte[]>();
                                for (int i = 0; i < chargerId.size(); i++) {
                                    if (certification.toString().contains(chargerId.get(i))) {
                                        byte[] copyData = new byte[9];
                                        for (int j = 1; j < 10; j++) {
                                            copyData[j - 1] += bytes[j];
                                        }
                                        values.add(copyData);
                                        byte[] loginHexArray = new LoginCombine(values.get(i)).loginParameterCombine(); // 로그인
                                                                                                                        // 파라미터
                                                                                                                        // 조합
                                                                                                                        // 객체
                                        byte[] hearbeatHexArray = new HeartBeatCombine(values.get(i))
                                                .HeartBeatParameterCombine();
                                        combine = new RecordCombine(values.get(i));
                                        // Heartbeat 파라미터 조합 객체
                                        socketLogin(loginHexArray, hearbeatHexArray, chargerId.get(i), chargerId); // 서버로부터
                                                                                                                   // 충전기로
                                                                                                                   // 로그인
                                                                                                                   // 응답
                                                                                                                   // 하는
                                                                                                                   // 메서드
                                        certification.setLength(0);
                                        baos.reset();
                                    }
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // MSTA -> 충전기로 데이터를 로그인 정보를 보내는 소켓 메서드
    public void socketLogin(byte[] loginHexArray, byte[] heartbeathexArray, String chargerId, ArrayList<String> id) {
        loginCount++;
        try {
            OutputStream out = socket.getOutputStream();
            DataOutputStream daos = new DataOutputStream(out);

            daos.write(loginHexArray);
            daos.flush();
            // 대이터 보내는 객체
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // byte 배열 변수 databox 타입과 크기 초기화
            byte[] dataBox = new byte[ChargerEVCConstant.SIZE];
            int read;
            // 읽어오는 데이터 byte 수만큼 while 문처리
            while ((read = dis.read(dataBox)) > -1) {
                baos.write(dataBox, 0, read);
                byte[] bytes = baos.toByteArray();
                login = new StringBuilder();
                for (byte b : bytes) {
                    login.append(String.format(ChargerEVCConstant.FORMAT, b));
                }
                // System.out.println(login.toString());
                if (login.toString().contains("68") && login.toString().contains(chargerId)
                        && login.toString().contains("A4") && login.toString().contains("16")) { // heartbeat를 응답하기 위한
                                                                                                 // 조건 체크
                    Thread.sleep(100);
                    heartBeat(heartbeathexArray);
                    login.setLength(0);
                    baos.reset(); // heartbeat를 응답하는 메서드
                    if (loginCount == id.size()) {
                        loginResult = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void socketChargingStart(byte[] startHexArray, byte[] stopHexArray, int sendNo, String code, int index,
            String plugNumber, HashMap<String, Object> param) {
        ExecutorService threadService = Executors.newFixedThreadPool(1);
        threadService.execute(() -> {
            int update = 0;
            int recordLength = 0;
            boolean recordFlag = false;
            try {
                if (code.equals("0AH02")) {
                    OutputStream out = socket.getOutputStream();
                    DataOutputStream daos = new DataOutputStream(out);

                    daos.write(startHexArray);
                    daos.flush();

                    InputStream in = socket.getInputStream();
                    DataInputStream dis = new DataInputStream(in);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] dataBox = new byte[ChargerEVCConstant.SIZE];
                    int read;
                    while ((read = dis.read(dataBox)) > -1) {
                        baos.write(dataBox, 0, read);
                        byte[] bytes = baos.toByteArray();
                        byte[] record = new byte[13];
                        int ind = 0;
                        for (byte b : bytes) {
                            recordLength++;
                            start.append(String.format(ChargerEVCConstant.FORMAT, b));
                            if (recordLength >= 30 && recordLength <= 43) {
                                record[ind] = b;
                                ind++;
                            }
                        }
                        recordLength = 0;
                        if (start.toString().contains("68") && start.toString().contains("6883")
                                && start.toString().contains("16")) {
                            byte[] recordHexArray = combine.recordParameter(record);
                            recordCharge(recordHexArray);
                            start.setLength(0);
                            if (recordFlag) {
                                byte[] readhexArray = new ReadCurrentCombine(values.get(index), plugNumber)
                                        .currentParameterCombine();
                                readCurrentData(readhexArray, param);
                                start.setLength(0);
                                baos.reset();
                            }
                        } else if (start.toString().contains("68") && start.toString().contains("688A")
                                && start.toString().substring(48, 50).equals("00")) {
                            if (update == 0) {
                                SendRecvController recv = new SendRecvController(connector);
                                recv.updateControl(sendNo);
                                update++;
                                recordFlag = true;
                                start.setLength(0);
                                baos.reset();
                            }
                        } else if (start.toString().contains("68") && start.toString().contains("681A")
                                && start.toString().substring(26, 28).equals("00")) {
                            SendRecvController recv = new SendRecvController(connector);
                            recv.updateComplete(plugNumber, sendNo);
                            threadService.shutdown();
                            start.setLength(0);
                            baos.reset();
                        }

                        if (start.toString().contains("68") && start.toString().contains("A4")
                                && start.toString().contains("16")) {
                            Thread.sleep(100);
                            heartBeat(heart);
                        }
                    }
                } else {
                    OutputStream out = socket.getOutputStream();
                    DataOutputStream daos = new DataOutputStream(out);

                    daos.write(stopHexArray);
                    daos.flush();

                    InputStream in = socket.getInputStream();
                    DataInputStream dis = new DataInputStream(in);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] dataBox = new byte[ChargerEVCConstant.SIZE];
                    int read;
                    while ((read = dis.read(dataBox)) > -1) {
                        baos.write(dataBox, 0, read);
                        byte[] bytes = baos.toByteArray();
                        byte[] record = new byte[13];
                        int ind = 0;
                        for (byte b : bytes) {
                            start.append(String.format(ChargerEVCConstant.FORMAT, b));
                            recordLength++;
                            if (recordLength >= 30 && recordLength < 43) {
                                record[ind] = b;
                                ind++;
                            }
                        }
                        if (start.toString().contains("68") && start.toString().contains("6883")
                                && start.toString().contains("16")) {
                            byte[] recordHexArray = combine.recordParameter(record);
                            recordCharge(recordHexArray);
                            recordLength = 0;
                            start.setLength(0);
                            baos.reset();
                        } else if (start.toString().contains("68") && start.toString().contains("688A")
                                && start.toString().substring(48, 50).equals("00")) {
                            SendRecvController recv = new SendRecvController(connector);
                            recv.updateStop(plugNumber, sendNo);
                            start.setLength(0);
                            baos.reset();
                            threadService.shutdown();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void readCurrentData(byte[] readhexArray, HashMap<String, Object> param) {
        ExecutorService threadService = Executors.newFixedThreadPool(1);
        threadService.execute(() -> {
            try {
                OutputStream out = socket.getOutputStream();
                DataOutputStream daos = new DataOutputStream(out);

                daos.write(readhexArray);
                daos.flush();

                InputStream in = socket.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] dataBox = new byte[ChargerEVCConstant.SIZE];
                int read;
                while ((read = dis.read(dataBox)) > -1) {
                    baos.write(dataBox, 0, read);
                    byte[] bytes = baos.toByteArray();
                    for (byte b : bytes) {
                        reading.append(String.format(ChargerEVCConstant.FORMAT, b));
                    }
                    if (reading.toString().contains("68") && reading.toString().contains("81")
                            && reading.toString().contains("16")) {
                        String voltage = reading.toString().substring(34, 38);
                        String ampare = reading.toString().substring(42, 44);
                        String percent = reading.toString().substring(48, 52);
                        CalModel models = new CalModel();
                        reading.setLength(0);
                        baos.reset();
                        if (!percent.contains("FF")) {
                            SendRecvController recvs = new SendRecvController(connector);
                            recvs.readDataInsertofUpdate(param);

                            models.setAmpare(Integer.parseInt(ampare, 16));
                            models.setVoltage(Integer.parseInt(voltage, 16));
                            models.setPercent(Integer.parseInt(percent, 16));
                            ReadDataCalculate rdc = new ReadDataCalculate(models);
                            DataUpdateController update = new DataUpdateController(rdc.kwhCal(), models.getPercent(),
                                    connector);
                            update.dataUpdate();
                            reading.setLength(0);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void heartBeat(byte[] heartbeathexArray) {
        try {
            heart = heartbeathexArray;
            OutputStream out = socket.getOutputStream();
            DataOutputStream daos = new DataOutputStream(out);

            daos.write(heartbeathexArray);
            daos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordCharge(byte[] recordHexArray) {
        try {
            OutputStream out = socket.getOutputStream();
            DataOutputStream daos = new DataOutputStream(out);

            daos.write(recordHexArray);
            daos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void qrRecognize(byte[] qrHexDataArray) {
        try {
            OutputStream out = socket.getOutputStream();
            DataOutputStream daos = new DataOutputStream(out);

            daos.write(qrHexDataArray);
            daos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void qrExplode(byte[] qrExplodeArray) {
        try {
            OutputStream out = socket.getOutputStream();
            DataOutputStream daos = new DataOutputStream(out);

            daos.write(qrExplodeArray);
            daos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}