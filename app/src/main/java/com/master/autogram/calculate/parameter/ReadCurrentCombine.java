package com.master.autogram.calculate.parameter;

import com.master.autogram.utils.constant.ChargerEVCConstant;

public class ReadCurrentCombine {
    private byte[] front;
    private String plugNumber;
    public ReadCurrentCombine(byte[] front, String plugNumber) {
        this.front = front;
        this.plugNumber = plugNumber;
    }
    
    public byte [] currentParameterCombine() {
        byte [] hexdata = new byte[23];
        boolean flag = true;
        Long [] start = new Long[2];
        Long [] charger = new Long[6];
        Long [] msta = new Long[2];
        Long control = null;
        Long DL = null;
        Long [] DA = new Long[2];
        Long [] DN = new Long[6];
        int index = 0, count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
        for(int i = 0; i < front.length; i+= 2) {
            if(i >= 0 && i < 6) {
                index++;
                hexdata[index] = front[i];
                charger[count] = Long.parseLong(String.format(ChargerEVCConstant.FORMAT, front[i]), 16);
                count++;
            }else if(i >= 6 && i < 8) {
                if(i == 6) {
                    count = 0;
                }
                index++;
                hexdata[index] = front[i];
                msta[count] = Long.parseLong(String.format(ChargerEVCConstant.FORMAT, front[i]), 16);
                count++;
            }else if(i == 8) {
                index++;
                hexdata[index] = front[i];
                start[1] = Long.parseLong(String.format(ChargerEVCConstant.FORMAT, front[i]), 16);
            }
        }
        while(flag) {
            index++;
            if(index == 10) {
                hexdata[index] = ChargerEVCConstant.CONTROL_READING;
                control = Long.parseLong("01", 16);
            }else if(index == 11) {
                hexdata[index] = (byte) 0x08;
                DL = Long.parseLong("08", 16);
            }else if(index == 12) {
                hexdata[index] = ChargerEVCConstant.FRAME_ZERO;
                count = 0;
            }else if(index == 13) {
                hexdata[index] = (byte) 0x01;
                DA[0] = Long.parseLong("01", 16);
            }else if(index == 14) {
                hexdata[index] = (byte) Long.parseLong(plugNumber, 16);
                DA[1] = Long.parseLong(plugNumber, 16);
            }else if(index == 15) {
                hexdata[index] = (byte) 0x20;
                DN[0] = Long.parseLong("20", 16);
            }else if(index == 16) {
                hexdata[index] = (byte) 0xB9;
                DN[1] = Long.parseLong("B9", 16);
            }else if(index == 17) {
                hexdata[index] = (byte) 0x40;
                DN[2] = Long.parseLong("40", 16);
            }else if(index == 18) {
                hexdata[index] = (byte) 0x89;
                DN[3] = Long.parseLong("89", 16);
            }else if(index == 19) {
                hexdata[index] = (byte) 0x9D;
                DN[4] = Long.parseLong("9D", 16);
            }else if(index == 20) {
                hexdata[index] = (byte) 0xB9;
                DN[5] = Long.parseLong("B9", 16);
            }else if(index == 21) {
                Long checksum = Long.parseLong("00", 16);
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], 
                    charger[5], msta[0], msta[1], control, DL, DA[0], DA[1], DN[0], DN[1], DN[2], DN[3], DN[4], DN[5]};
                String hexFormat = "";
                for(int i = 0; i < checksumList.length; i++) {
                    checksum += checksumList[i];
                }
                hexFormat = String.format(ChargerEVCConstant.FORMAT, checksum).substring(1, 3);
                checksum = Long.parseLong(hexFormat, 16);
                hexdata[index] = checksum.byteValue();
            }else if(index == 22) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }else  {
                flag = false;
            }
        }
        return hexdata;
    }
    
}