package com.master.autogram.calculate.parameter;

import com.master.autogram.utils.constant.ChargerEVCConstant;

public class RecordCombine {
    private byte [] front;
    public RecordCombine(byte [] front) {
        this.front = front;
    }

    public byte [] recordParameter(byte [] record) {
        byte [] hexdata = new byte[30];
        Boolean flag = true;
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[ChargerEVCConstant.CHARGER_ID];
        Long [] msta = new Long[ChargerEVCConstant.MSTA_SEQ];
        Long control = null;
        Long [] serialCode = new Long[15];
        Long dl = null;
        int count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
        //충전기로 받아온 데이터 sending 파라미터 리스트 조합
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
                hexdata[index] = (byte)0x05;
                control = Long.parseLong("05", 16);
            }else if(index == 11) {
                hexdata[index] = (byte )0x0F;
                dl = Long.parseLong("0F", 16);
            }else if(index == 12) {
                hexdata[index] = (byte)0x00;
                count = 0;
            }else if(index >= 13  && index < 28) {
                for(int i = 0; i < record.length; i+= 2) {
                    hexdata[index] = (byte) record[i];
                    serialCode[count] = Long.parseLong(String.format(ChargerEVCConstant.FORMAT, record[i]), 16);
                    count++;
                }
            }else if(index == 28) { // checksum 구하는 부분
                Long checksum = Long.parseLong("00", 16);
                String checkCal = "";
                Long [] checksumList = {start[0], start[1], charger
                    [0], charger[1], charger[2], charger[3], charger[4], 
                                    charger[5], 
                                    msta[0], msta[1], control, dl, serialCode[0], serialCode[1], serialCode[2], serialCode[3], serialCode[4],
                                    serialCode[5], serialCode[6], serialCode[7], serialCode[8], serialCode[9], serialCode[10], serialCode[11],
                                    serialCode[12], serialCode[13], serialCode[14]};
                for(int j = 0; j < checksumList.length; j++) {
                    // System.out.println(j + " : " + checksumList[j]);
                    checksum += checksumList[j];
                }
                checkCal = String.format("%02X", checksum).substring(1, 3);
                hexdata[index] = (byte) Long.parseLong(checkCal, 16);
            }else if(index == 29) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }
            if(index == 30) {
                flag = false;
            }
        }
        return hexdata;
    }
}
