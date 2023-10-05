package com.master.autogram.utils.db;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


public class MysqlConnector {
    private Connection conn = null;

    final String pro_src = "\\home\\ubuntu\\single-bienergy-mst\\app\\src\\main\\java\\com\\master\\autogram\\properties\\Setting.properties";
    private static Properties db_setting = null;

    public MysqlConnector() {
        FileReader resources;
        db_setting = new Properties();
        try {
            resources = new FileReader(pro_src);
            db_setting.load(resources);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 기본 설정2 v 
    
    //DB 요소 설정
    public Connection getConnection() {
        try {
            Class.forName(db_setting.getProperty("JDBC_DRIVER").toString());
            conn = DriverManager.getConnection(db_setting.getProperty("DB_URL").toString(), 
                db_setting.getProperty("DB_USER").toString(),
                db_setting.getProperty("DB_PASSWORD").toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return conn;
    }
}
