package com.master.autogram.calculate.parameter;

import com.master.autogram.utils.constant.ChargerEVCConstant;

public class QrRecognizeCombine {
    private byte [] front;
    public QrRecognizeCombine(byte [] front) {
        this.front = front;
    }

    public byte [] QrParameterCombine(String qdata) {
        byte [] hexdata = new byte[214];
        boolean flag = true;
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[6];
        Long [] msta = new Long[2];
        Long control = null;
        Long dl = null;
        Long mpn = null;
        Long auth = null;
        Long [] dn = new Long[2];
        Long [] qr = new Long[qdata.length() / 2];
        int k = qdata.length();
        int count = 0;
        hexdata[index] = ChargerEVCConstant.FRAME_START;
        start[0] = Long.parseLong("68", 16);
        count = 0;
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
                hexdata[index] = (byte) Long.parseLong("08", 16);
                control = Long.parseLong("08", 16);
            }else if(index == 11) {
                hexdata[index] = (byte) Long.parseLong("C7", 16);
                dl = Long.parseLong("C7", 16);
                count = 0;
            }else if(index == 12) {
                hexdata[index] = (byte) Long.parseLong("00", 16);
            }else if(index == 13) {
                hexdata[index] = (byte) Long.parseLong("02", 16);
                mpn = Long.parseLong("02", 16);
            }else if(index == 14) {
                hexdata[index] = (byte) Long.parseLong("11", 16);
                auth = Long.parseLong("11", 16);
            }else if(index >= 15 && index < 18) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 18) {
                hexdata[index] = (byte) 0x41;
                dn[0] = Long.parseLong("41", 16);
            }else if(index == 19) {
                hexdata[index] = (byte) 0x89;
                dn[1] = Long.parseLong("89", 16);
            }else if(index >= 20 && index < 20 + (192 - (qdata.length() / 2))) {
                hexdata[index] = (byte) 0x00;
            }else if(index >=  20 + (192 - (qdata.length() / 2)) && index < 20 + 192) {
                String hex = qdata.substring(k - 2, k);
                hexdata[index] = (byte)Long.parseLong(hex, 16);
                k -= 2;
                qr[count] = Long.parseLong(hex, 16);
                count++;
            }else if(index == 212) {
                Long checksum = Long.parseLong("00", 16);
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], charger[5], msta[0], msta[1], control,
                                        dl, mpn, auth, dn[0], dn[1]};
                String foramtHex = "";
                for(int j = 0; j < checksumList.length; j++) {
                    // System.out.println(i + " : " + checksumList[i]);
                    checksum += checksumList[j];
                }
                for(int j = 0; j < qr.length; j++) {
                    checksum += qr[j];
                }
                foramtHex = String.format(ChargerEVCConstant.FORMAT, checksum).substring(1, 3);
                checksum = Long.parseLong(foramtHex, 16);
                hexdata[index] = checksum.byteValue();
            }else if(index == 213) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }else if(index == 214) {
                flag = false;
            }
        }
        return hexdata;
    }
}
