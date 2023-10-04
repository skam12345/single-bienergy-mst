package com.master.autogram.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.master.autogram.utils.db.MysqlConnector;
import com.master.autogram.utils.db.SqlDataList;

public class DataUpdateController {
    private int kwh;
    private int percent;
    private Connection conn;
    private PreparedStatement pstmt;
    public DataUpdateController(int kwh, int percent, MysqlConnector connector) {
        this.kwh = kwh;
        this.percent = percent;
        this.conn = connector.getConnection();
    }

    public void dataUpdate() {
        try {
            pstmt = conn.prepareStatement(SqlDataList.data_update);
            pstmt.setInt(1, kwh);
            pstmt.setInt(2, percent);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try{
                pstmt.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
        
    }

    
}
