package com.java.cloudStore;

import java.io.*;
import java.net.Socket;

public class NetWork {

    private Socket socket;
    private static final int PORT =8189;
    private static final String ADDRESS = "localhost";

    private BufferedInputStream in;
    private BufferedOutputStream out;


    public NetWork(){
        try{
            socket=new Socket(ADDRESS,PORT);
            out = new BufferedOutputStream(socket.getOutputStream());
            in = new BufferedInputStream(socket.getInputStream());


        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void readingFromServer(){
        try {
            //sendFile();

            DataInputStream mainDin = new DataInputStream(in);
            while (true) {
                String command = mainDin.readUTF();



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(String msg){
        try {
            DataOutputStream commandStream = new DataOutputStream(out);
            commandStream.writeUTF(msg);
            commandStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }






}
