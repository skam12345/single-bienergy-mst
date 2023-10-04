package com.master.autogram.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.master.autogram.model.receive.RecvDataModel;
import com.master.autogram.utils.db.MysqlConnector;
import com.master.autogram.utils.db.SqlDataList;

public class SendRecvController {
    private Connection conn;
    private ResultSet rs;
    private PreparedStatement pstmt;
    public SendRecvController(MysqlConnector connector) { 
        conn = connector.getConnection();
    }

    public ArrayList<RecvDataModel> callChargerInfo(String key, String code) {
        RecvDataModel model = null;
        ArrayList<RecvDataModel> data = null;
        try {
            data = new ArrayList<RecvDataModel>();
            if(code.equals("0AH02")) {
                pstmt = conn.prepareStatement(SqlDataList.callInfo);
            }else if(code.equals("0AH03")) {
                pstmt = conn.prepareStatement(SqlDataList.callStopInfo);
            }else {
                pstmt = conn.prepareStatement(SqlDataList.callReadInfo);
            }
            rs = pstmt.executeQuery();
            while(rs.next()) {
                model = new RecvDataModel();
                model.setSendNo(rs.getInt(1));
                model.setPlugNumber(rs.getString(2));
                model.setCharging(rs.getInt(3));
                data.add(model);
            }
            System.out.println(data.size());
            System.out.println("?");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void readDataInsertofUpdate(HashMap<String, Object> param) {
        int count = 0;
        int result = 0;
        try {
            pstmt = conn.prepareStatement(SqlDataList.read_table_check);
            pstmt.setString(1, param.get("chargerNo").toString());
            rs = pstmt.executeQuery();
            while(rs.next()) {
                count = rs.getInt(1);
            }

            if(count == 0) {
                pstmt = conn.prepareStatement(SqlDataList.insert_read_table);
                pstmt.setString(1, param.get("code").toString());
                pstmt.setString(2, param.get("chargerNo").toString());
                pstmt.setString(3, param.get("chargerPlug").toString());
                pstmt.setInt(4, 0);
                pstmt.setString(5, "충전중");
                pstmt.setString(6, "N");
                System.out.println(param.get("pyudNo").toString());
                pstmt.setString(7, param.get("pyudNo").toString());
                pstmt.setInt(8, 0);
                result = pstmt.executeUpdate();
                if(result > 0) {
                    System.out.println("입력 성공"); 
                }else {
                    System.out.println("입력 실패");
                }
            }else {

            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                pstmt.close();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateControl(int sendNo) {
        int result = 0;
        try {
            pstmt = conn.prepareStatement(SqlDataList.update_control);
            pstmt.setInt(1, sendNo);
            result = pstmt.executeUpdate();
            if(result > 0) {
                System.out.println("업데이트 성공");
            }else {
                System.out.println("업데이트 실패");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStop(String plugNumber, int sendNo) {
        int result = 0;
        try {
            pstmt = conn.prepareStatement(SqlDataList.updateStop);
            pstmt.setInt(1, sendNo);
            pstmt.setString(2, plugNumber);
            result = pstmt.executeUpdate();
            if(result > 0) {
                System.out.println("업데이트 성공");
            }else {
                System.out.println("업데이트 실패");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateComplete(String plugNumber, int sendNo) {
        int result = 0;
        try {
            pstmt = conn.prepareStatement(SqlDataList.updateComplete);
            pstmt.setInt(1, sendNo);
            pstmt.setString(2, plugNumber);
            result = pstmt.executeUpdate();
            if(result > 0) {
                System.out.println("업데이트 성공");
            }else {
                System.out.println("업데이트 실패");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String currentChargerId(int sendNo) {
        String chargerId = "";
        try {
            pstmt = conn.prepareStatement(SqlDataList.currentChargerId);
            pstmt.setInt(1, sendNo);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                chargerId = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chargerId;
    }
}