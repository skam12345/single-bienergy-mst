package com.master.autogram.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.master.autogram.utils.db.MysqlConnector;
import com.master.autogram.utils.db.SqlDataList;

public class DBLoopController {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    public DBLoopController(MysqlConnector connector) {
        conn = connector.getConnection();
    }

    public ArrayList<HashMap<String, Object>> callList() {
        ArrayList<HashMap<String, Object>> code = null;
        try {
            code = new ArrayList<HashMap<String, Object>>();
            pstmt = conn.prepareStatement(SqlDataList.call_Control_Code);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("keyNumber", rs.getInt(1));
                map.put("code", rs.getString(2));
                map.put("chargerNo", rs.getString(3));
                map.put("chargerPlug", rs.getString(4));
                map.put("chargingValue", rs.getInt(5));
                map.put("pyudNo", rs.getString(6));
                code.add(map);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public String checkCode(int key) {
        String code = "";
        try {
            pstmt = conn.prepareStatement(SqlDataList.check_code);
            pstmt.setInt(1, key);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                code = rs.getString(1);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public ArrayList<String> callChargerId() {
        ArrayList<String> chargerId = null;
        try {
            chargerId = new ArrayList<String>();
            pstmt = conn.prepareStatement(SqlDataList.callChargerId);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                chargerId.add(rs.getString(1));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return chargerId;
    }
}