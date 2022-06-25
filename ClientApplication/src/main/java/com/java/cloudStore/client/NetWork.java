package com.java.cloudStore.client;

import com.java.cloudStore.api.CloudMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.*;
import java.net.Socket;

public class NetWork {



    private Socket socket;
    private static final int PORT =8189;
    private static final String ADDRESS = "localhost";

    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;




    public NetWork(){
        try{
            socket=new Socket(ADDRESS,PORT);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());



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
    public Socket getSocket() {
        return socket;
    }

    public CloudMessage read() throws IOException, ClassNotFoundException {
        return (CloudMessage) in.readObject();
    }

    public void write(CloudMessage msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public void disconnect() throws IOException {
        socket.close();
    }

}







