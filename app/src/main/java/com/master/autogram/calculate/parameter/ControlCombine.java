package com.master.autogram.calculate.parameter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


import com.master.autogram.utils.constant.ChargerEVCConstant;
// 충전기 제어 파라미터 조합 하는 객체
public class ControlCombine {
    private byte [] front;
    private String plug_number;
    private int charging;
    String chargerId;
    String reverseHex;
    public ControlCombine(byte [] front, String plug_number, int charging) {
        this.front = front;
        this.plug_number = plug_number;
        this.charging = charging;
        chargerId = "";
        reverseHex = "";
    }
    // 충전 시작 sending 파라미터 조합 메서드
    public byte [] startParameterCombine() {
        Boolean flag = true;
        Boolean charFlag = true;
        byte [] hexdata = new byte[61];
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[ChargerEVCConstant.CHARGER_ID * 2];
        Long [] msta = new Long[ChargerEVCConstant.MSTA_SEQ];
        Long control = null;
        Long dl = null;
        Long auth = null;
        Long command = null;
        Long [] plug = new Long[2];
        Long [] current = new Long[6];
        Long [] random = new Long[2];
        Long parameter = null;
        Long [] chargings = new Long[4];
        Long [] balance = new Long[4];
        int idx = 0;
        int count = 0;
        SimpleDateFormat format = new SimpleDateFormat("ssmmHHddMMyy");
        String currentDate = format.format(new Date());
        int value = this.charging * 100;
        String chHex = Integer.toHexString(value);
        if(chHex.length() == 3) {
            chHex = "0" + chHex;
        }else if(chHex.length() == 5) {
            chHex = "0" + chHex;
        }else if(chHex.length() == 7) {
            chHex = "0" + chHex;
        }
        for(int i = chHex.length();  i > 0; i-=2) {
            reverseHex += chHex.substring(i - 2, i);
        }
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
                hexdata[index] = ChargerEVCConstant.CONTROL_CHARGING;
                control = Long.parseLong("0A", 16);
            }else if(index == 11) {
                hexdata[index] = (byte )0x2E;
                dl = Long.parseLong("2E", 16);
            }else if(index == 12) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 13) {
                hexdata[index] = (byte) 0x11;
                auth = Long.parseLong("11", 16);
            }else if(index >= 14 && index < (14 + ChargerEVCConstant.PWD)) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 17) {
                hexdata[index] = (byte) 0x02;
                command = Long.parseLong("02", 16);
            }else if(index >= 18 && index < 18 + ChargerEVCConstant.DATA_ACCOUNT) {
                hexdata[index] = (byte) 0x00;
            }else if(index >= 26 && index < 26 + ChargerEVCConstant.DATA_CARD) {
                hexdata[index] = (byte) 0x00;
                count = 6;
            }else if(index >= 34 && index < 34 + ChargerEVCConstant.CHARGER_ID) {
                hexdata[index] = (byte) Long.parseLong(chargerId.substring(idx, idx + 2), 16);
                charger[count] = Long.parseLong(chargerId.substring(idx, idx + 2), 16);
                idx += 2;
                count++;
            }else if(index == 40) {
                hexdata[index] = (byte) Long.parseLong(this.plug_number, 16);
                plug[0] = Long.parseLong(this.plug_number, 16);
                count = 0;
                idx = 0;
            }else if(index >= 41 && index < 47) {
                hexdata[index] = (byte) Long.parseLong(currentDate.substring(idx, idx + 2), 16);
                current[count] = Long.parseLong(currentDate.substring(idx, idx + 2), 16);
                count++;
                idx += 2;
                if(count == 6) {
                    count = 0;
                }
            }else if(index >= 47 && index < 49) {
                String number = "0";
                Random rando = new Random();
                int rand = rando.nextInt(99);
                if(rand < 10) {
                    number += Integer.toString(rand);
                }else {
                    number = Integer.toString(rand);
                }
                hexdata[index] = (byte) Long.parseLong(number, 16);
                random[count] = Long.parseLong(number, 16);
                count++;
            }else if(index == 49) {
                hexdata[index] = (byte) Long.parseLong(this.plug_number, 16); 
                plug[1] =  Long.parseLong(this.plug_number, 16);
            }else if(index == 50) {
                hexdata[index] = (byte) 0x03;
                parameter = Long.parseLong("03", 16);
                idx = 0;
                count = 0;
            }else if(index >= 51 && index < 55) {
                if(charFlag) {
                    String etc = reverseHex.substring(idx, idx + 2);
                    hexdata[index] = (byte) Long.parseLong(etc, 16);
                    chargings[count] = Long.parseLong(etc, 16);
                    count++;
                    idx += 2;
                }
                if(idx == 4) {
                    if(chHex.length() != 8) {
                        if(index > 54 - ((8 - chHex.length()) / 2)) {
                            hexdata[index] = (byte) 0x00;
                            chargings[count] = Long.parseLong("00", 16);
                            count++;
                        }
                        charFlag = false;
                    }
                }
                // if(charFlag) {
                //     if(chHex.length() != 8) {
                //         if(index <= 54 - ((8 - chHex.length()) / 2)) {
                //             hexdata[index] = (byte) 0x00;
                //             chargings[count] = Long.parseLong("00", 16);
                //             count++;
                //         }else {
                //             String etc = chHex.substring(idx -2, idx);
                //             hexdata[index] = (byte) Long.parseLong(etc, 16);
                //             chargings[count] = Long.parseLong(etc, 16);
                //             count++;
                //             idx -= 2;
                //         }
                //     }
                // }
                if(index == 54) {
                    count = 0;
                }
            }else if(index >= 55 && index < 55 + ChargerEVCConstant.BALANCE) {
                hexdata[index] = (byte) 0xFF;
                balance[count] = Long.parseLong("FF", 16);
                count++;

            }else if(index == 59) { // checksum 구하는 부분
                Long checksum = Long.parseLong("00", 16);
                String checkCal = "";
                Long [] checksumList = {start[0], start[1], charger
                    [0], charger[1], charger[2], charger[3], charger[4], 
                                    charger[5], charger[6], charger[7], charger[8], charger[9], charger[10], charger[11], 
                                    msta[0], msta[1], control, dl, auth, command, plug[0], plug[1], current[0], current[1], 
                                    current[2], current[3], current[4], current[5], random[0], random[1],
                                    parameter, chargings[0], chargings[1], chargings[2], chargings[3], balance[0], balance[1], balance[2], balance[3]};
                for(int j = 0; j < checksumList.length; j++) {
                    // System.out.println(j + " : " + checksumList[j]);
                    checksum += checksumList[j];
                }
                checkCal = String.format("%02X", checksum).substring(1, 3);
                hexdata[index] = (byte) Long.parseLong(checkCal, 16);
            }else if(index == 60) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }
            if(index == 61) {
                flag = false;
            }

        }
        return hexdata;
    }
     // 충전 중지 sending 파라미터 조합 메서드
    public byte [] stopParameterCombine() {
        Boolean flag = true;
        Boolean charFlag = true;
        byte [] hexdata = new byte[61];
        int index = 0;
        Long [] start = new Long[2];
        Long [] charger = new Long[ChargerEVCConstant.CHARGER_ID * 2];
        Long [] msta = new Long[ChargerEVCConstant.MSTA_SEQ];
        Long control = null;
        Long dl = null;
        Long auth = null;
        Long command = null;
        Long [] plug = new Long[2];
        Long [] current = new Long[6];
        Long [] random = new Long[2];
        Long parameter = null;
        Long [] chargings = new Long[4];
        Long [] balance = new Long[4];
        int idx = 0;
        int count = 0;
        SimpleDateFormat format = new SimpleDateFormat("ssmmHHddMMyy");
        String currentDate = format.format(new Date());
        int value = this.charging * 100;
        String chHex = Integer.toHexString(value);
        if(chHex.length() == 3) {
            chHex = "0" + chHex;
        }else if(chHex.length() == 5) {
            chHex = "0" + chHex;
        }else if(chHex.length() == 7) {
            chHex = "0" + chHex;
        }
        for(int i = chHex.length();  i > 0; i-=2) {
            reverseHex += chHex.substring(i-2, i);
        }
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
            if(index == 7) {
                hexdata[index] = (byte) 0xC1;
                msta[0] = Long.parseLong("C1", 16);
            }else if(index == 8) {
                hexdata[index] = (byte) 0x06;
                msta[1] = Long.parseLong("06", 16);
            }else if(index == 9) {
                hexdata[index] = ChargerEVCConstant.FRAME_START;
                start[1] = Long.parseLong("68", 16);
            }else if(index == 10) {
                hexdata[index] = ChargerEVCConstant.CONTROL_CHARGING;
                control = Long.parseLong("0A", 16);
            }else if(index == 11) {
                hexdata[index] = (byte )0x2E;
                dl = Long.parseLong("2E", 16);
            }else if(index == 12) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 13) {
                hexdata[index] = (byte) 0x11;
                auth = Long.parseLong("11", 16);
            }else if(index >= 14 && index < (14 + ChargerEVCConstant.PWD)) {
                hexdata[index] = (byte) 0x00;
            }else if(index == 17) {
                hexdata[index] = (byte) 0x03;
                command = Long.parseLong("03", 16);
            }else if(index >= 18 && index < 18 + ChargerEVCConstant.DATA_ACCOUNT) {
                hexdata[index] = (byte) 0x00;
            }else if(index >= 26 && index < 26 + ChargerEVCConstant.DATA_CARD) {
                hexdata[index] = (byte) 0x00;
                count = 6;
            }else if(index >= 34 && index < 34 + ChargerEVCConstant.CHARGER_ID) {
                hexdata[index] = (byte) Long.parseLong(chargerId.substring(idx, idx + 2), 16);
                charger[count] = Long.parseLong(chargerId.substring(idx, idx + 2), 16);
                idx += 2;
                count++;
            }else if(index == 40) {
                hexdata[index] = (byte) Long.parseLong(this.plug_number, 16);
                plug[0] = Long.parseLong(this.plug_number, 16);
                count = 0;
                idx = 0;
            }else if(index >= 41 && index < 47) {
                hexdata[index] = (byte) Long.parseLong(currentDate.substring(idx, idx + 2), 16);
                current[count] = Long.parseLong(currentDate.substring(idx, idx + 2), 16);
                count++;
                idx += 2;
                if(count == 6) {
                    count = 0;
                }
            }else if(index >= 47 && index < 49) {
                String number = "0";
                Random rando = new Random();
                int rand = rando.nextInt(99);
                if(rand < 10) {
                    number += Integer.toString(rand);
                }else {
                    number = Integer.toString(rand);
                }
                hexdata[index] = (byte) Long.parseLong(number, 16);
                random[count] = Long.parseLong(number, 16);
                count++;
            }else if(index == 49) {
                hexdata[index] = (byte) Long.parseLong(this.plug_number, 16); 
                plug[1] =  Long.parseLong(this.plug_number, 16);
            }else if(index == 50) {
                hexdata[index] = (byte) 0x03;
                parameter = Long.parseLong("03", 16);
                idx = 0;
                count = 0;
            }else if(index >= 51 && index < 55) {
                if(charFlag) {
                    String etc = reverseHex.substring(idx, idx + 2);
                    hexdata[index] = (byte) Long.parseLong(etc, 16);
                    chargings[count] = Long.parseLong(etc, 16);
                    count++;
                    idx += 2;
                }
                if(idx == 4) {
                    if(chHex.length() != 8) {
                        if(index > 54 - ((8 - chHex.length()) / 2)) {
                            hexdata[index] = (byte) 0x00;
                            chargings[count] = Long.parseLong("00", 16);
                            count++;
                        }
                        charFlag = false;
                    }
                }
                // if(charFlag) {
                //     if(chHex.length() != 8) {
                //         if(index <= 54 - ((8 - chHex.length()) / 2)) {
                //             hexdata[index] = (byte) 0x00;
                //             chargings[count] = Long.parseLong("00", 16);
                //             count++;
                //         }else {
                //             String etc = chHex.substring(idx -2, idx);
                //             hexdata[index] = (byte) Long.parseLong(etc, 16);
                //             chargings[count] = Long.parseLong(etc, 16);
                //             count++;
                //             idx -= 2;
                //         }
                //     }
                // }
                if(index == 54) {
                    count = 0;
                }
            }else if(index >= 55 && index < 55 + ChargerEVCConstant.BALANCE) {
                hexdata[index] = (byte) 0xFF;
                balance[count] = Long.parseLong("FF", 16);
                count++;
            }else if(index == 59) {
                Long checksum = Long.parseLong("00", 16);
                String checkCal = "";
                Long [] checksumList = {start[0], start[1], charger[0], charger[1], charger[2], charger[3], charger[4], 
                                    charger[5], charger[6], charger[7], charger[8], charger[9], charger[10], charger[11], 
                                    msta[0], msta[1], control, dl, auth, command, plug[0], plug[1], current[0], current[1], 
                                    current[2], current[3], current[4], current[5], random[0], random[1],
                                    parameter, chargings[0], chargings[1], chargings[2], chargings[3], balance[0], balance[1], balance[2], balance[3]};
                for(int j = 0; j < checksumList.length; j++) {
                    checksum += checksumList[j];
                }
                checkCal = String.format("%02X", checksum).substring(1, 3);
                hexdata[index] = (byte) Long.parseLong(checkCal, 16);
            }else if(index == 60) {
                hexdata[index] = ChargerEVCConstant.FRAME_END;
            }
            if(index == 61) {
                flag = false;
            }

        }
        return hexdata;
    }
}