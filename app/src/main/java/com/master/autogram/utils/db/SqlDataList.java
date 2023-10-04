package com.master.autogram.utils.db;

public class SqlDataList {
    
    public static final String callInfo = "SELECT se.send_no, se.charger_plug_no, se.pyud_charge FROM (SELECT * FROM send_command WHERE cin_code = '0AH02' AND send_control_yn = 'N') as se INNER JOIN (SELECT * FROM charger_info WHERE chn_no = (SELECT chnNo FROM send_command WHERE send_control_yn = 'N')) as ch"; 
    public static final String callStopInfo = "SELECT se.send_no, se.charger_plug_no, se.pyud_charge FROM (SELECT * FROM send_command WHERE cin_code = '0AH03' AND send_control_yn = 'N') as se INNER JOIN (SELECT * FROM charger_info WHERE chn_no = (SELECT chnNo FROM send_command WHERE send_control_yn = 'N')) as ch"; 
    public static final String callReadInfo = "SELECT se.send_no, se.charger_plug_no, se.pyud_charge FROM (SELECT * FROM send_command WHERE cin_code = '0AH01' AND send_control_yn = 'Y') as se INNER JOIN (SELECT * FROM charger_info WHERE chn_no = (SELECT chnNo FROM send_command WHERE send_control_yn = 'N')) as ch"; 
    public static final String call_Control_Code = "SELECT send_no, cin_code, chnNo, charger_plug_no, pyud_charge, pyud_no FROM send_command as se WHERE chnNo = (SELECT chn_no FROM charger_info WHERE chn_no = se.chnNo) AND send_control_yn  = 'N'";
    public static final String check_code = "SELECT cin_code FROM send_command WHERE send_no = ?";
    public static final String read_table_check = "SELECT COUNT(*) FROM receive_command WHERE chnNo = ? AND receive_complete_yn = 'N'";
    public static final String insert_read_table = "INSERT INTO receive_command(cin_code, chnNo, charger_plug_no, pyud_charged, cin_code_name, receive_complete_yn, pyud_no, receive_reg_date, receive_update_date,  pyud_charging_percent) VALUES(?, ?, ?, ?, ?, ?, ?, SYSDATE(), SYSDATE(), ?)";
    public static final String update_control = "UPDATE send_command SET send_control_yn = 'Y', cin_code = '0AH01' WHERE send_no = ?";
    public static final String data_update = "UPDATE receive_command SET pyud_charged = ?, pyud_charging_percent = ? WHERE chnNo = (SELECT chn_no FROM send_command as se WHERE chnNo = (SELECT chn_no FROM charger_info WHERE chn_no = se.chnNo))";
    public static final String callChargerId = "SELECT chn_charger_id FROM charger_info WHERE chn_charger_id is NOT NULL";
    public static final String updateStop = "UPDATE receive_command SET receive_complete_yn = 'Y', cin_code_name = '충전중지' WHERE chnNo = (SELECT chnNo FROM send_command WHERE send_no = ?) and charger_plug_no  = ?";
    public static final String updateComplete = "UPDATE receive_command SET receive_complete_yn = 'Y', cin_code_name = '충전완료' WHERE chnNo = (SELECT chnNo FROM send_command WHERE send_no = ?) and charger_plug_no  = ?";
    public static final String currentChargerId = "SELECT chn_charger_id FROM charger_info WHERE chn_no = (SELECT chnNo FROM send_command WHERE send_control_yn = 'N' AND send_no = ?)";
}
