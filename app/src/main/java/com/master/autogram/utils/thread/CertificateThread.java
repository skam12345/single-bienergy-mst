// package com.master.autogram.utils.thread;

// import java.io.ByteArrayOutputStream;
// import java.io.DataInputStream;
// import java.io.InputStream;
// import java.net.InetSocketAddress;
// import java.net.ServerSocket;
// import java.net.Socket;

// import com.master.autogram.utils.constant.ChargerEVCConstant;

// public class CertificateThread implements Runnable{
//     private Socket socket;
//     private String ip;
//     private StringBuilder certification;
//     public CertificateThread(Socket socket) {
//         this.socket = socket;
//         certification = new StringBuilder();
//         ip = "";
//     }
//     @Override
//     public void run() {
//         try {
//                     InputStream in = socket.getInputStream();
//                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                     DataInputStream dis = new DataInputStream(in);
//                     byte [] dataBox = new byte[ChargerEVCConstant.SIZE];
//                     int read;
//                     while(true) {
//                         ip =(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
//                         if(ip != null) {
//                             if((read = dis.read(dataBox)) > -1) {
//                                 baos.write(dataBox, 0, read);
//                                 byte [] bytes = baos.toByteArray();
//                                 for(byte b : bytes) {
//                                     certification.append(String.format(ChargerEVCConstant.FORMAT, b));
//                                     if(certification.toString().contains("68") && certification.toString().contains("82") && certification.toString().contains("16")) {
//                                         login.setLength(0);
//                                     }else if(certification.toString().contains("68") && certification.toString().contains("6889") && certification.toString().contains("16")) {
//                                         certification.setLength(0);
//                                     }else if(certification.toString().contains("7E") && certification.toString().contains("16")) {
//                                         certification.setLength(0);
//                                     }else if(certification.toString().contains("68") && certification.toString().contains("A1") && certification.toString().contains("16")) {
//                                         if(a1Count > 1) {
//                                             certification.delete(0, a1Length * (a1Count - 1));
//                                         }
//                                     }
//                                 }
//                                 if(certification.toString().contains("68") && certification.toString().contains("A1") && certification.toString().contains("16")) {
//                                     if(a1Count == 0) {
//                                         values = certification.toString().substring(2, 22);
//                                         a1Count++;
//                                     }else {
//                                         a1Count++;
//                                     }
//                                 }
//                             }
//                         }
//                     }
//                 } catch(Exception e) {
//                     close = true;
//                     reLogin();
//                 }
//     }
    
// }
