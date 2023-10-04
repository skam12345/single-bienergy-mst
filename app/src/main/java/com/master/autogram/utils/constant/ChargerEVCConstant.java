package com.master.autogram.utils.constant;

public class ChargerEVCConstant {
    /*Corresponding to Setting value on Constant*/
    public static final int SIZE = 2048;
    public static final int TYPE = 16;
    public static final String FORMAT = "%02X";
    
    /*Corresponding to Parameter on Constant*/
    public static final byte FRAME_START = (byte) 0x68;
    public static final byte FRAME_ZERO = (byte)0x00;
    public static final byte FRAME_TERMINAL = (byte)0x00;
    public static final byte FRAME_MEASUREMENT = (byte)0x01;
    public static final String FRAME_NOTACCOUNT = "Y";
    public static final String FRAME_NOTCARD = "Y";
    public static final byte FRAME_END = (byte)0x16;

    /*Corresponding to byte count on Constant*/
    public static final int CHARGER_ID = 6;
    public static final int MSTA_SEQ = 2;
    public static final int DA = 2;
    public static final int DL = 2;
    public static final int MPN = 1;
    public static final int PWD = 3;
    public static final int DATA_ACCOUNT = 8;
    public static final int DATA_CARD = 8;
    public static final int TRANSACTION_SERIAL = 15;
    public static final int BALANCE = 4;
    
    /*Corresponding to Control Request on Constant*/
    public static final byte CONTROL_LOGIN =(byte)0x21;
    public static final byte CONTROL_CHARGING = (byte)0x0A;
    public static final byte CONTROL_READING = (byte)0x01;
    public static final byte CONTRL_WRITING = (byte)0x08;

    /*Corresponding to Control Response on Constant */
    public static final String RESPONSE_CHARGING = "8A";
    public static final String RESPONSE_READING = "81";
    public static final String RESPONSE_WRITING = "88";
    /*Corresponding to Writing Error Code on Constant*/
    public static final String CORRECT = "00";
    public static final String NOT_RETURN = "01";
    public static final String ILLEGAL_CONTENT = "02";
    public static final String INVALID_PERMMISION = "03";
    public static final String NO_SUCH_DATA = "04";
    public static final String INVALIDITY_TIME = "05";
    public static final String TARGET_NOT_EXIST = "11";
    public static final String SEND_FAILED = "12";
    public static final String SHORT_MSG_LONG = "13";

    /*Corresponding to Charging Error Code on Constant*/
    public static final String  SUCCESS = "00";
    public static final String NOT_IN_SERVICE = "01";
    public static final String IN_CHARGING = "02";
    public static final String ALTMATED_WAiTING = "04";
    public static final String OTHER_REASON = "FF";
    
    /*Corresponding to Charger status Code on Constant*/
    public static final String IDLE_STATUS = "01";
    public static final String CHARGING = "02";
    public static final String FAULT_STATUS = "03";
    public static final String START_NOT_PLUG = "04";
    public static final String PRE_CHECKING = "05";
    public static final String CHARGING_END = "06";
    public static final String LOCAL_OPERATING = "08";
    public static final String WAITING_FOR_END = "0A";
    public static final String PLUG_IN_NOT_CHARGING = "0B";
    

    
}
