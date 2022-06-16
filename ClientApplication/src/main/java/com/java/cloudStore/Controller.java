package com.java.cloudStore;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {




    @FXML
    public Button toServerButton;

    @FXML
    public ListView serverFiles;

    @FXML
    public ListView clientFiles;

    public List<String> clientFilesList;
    public List<String> serverFilesList;

    public void initialize(URL location, ResourceBundle resources) {

        new Thread(()->{

        }).start();




    }





    private void refresh(){
        //sendCommand("/REFRESH");
        try {
           // ObjectInputStream oin =new ObjectInputStream(in);
           // serverFilesList = (List<String>) oin.readObject();


        } catch (Exception e) {
            e.printStackTrace();
        }
        getFilesList();
        Platform.runLater(()->{
            clientFiles.getItems().clear();
            serverFiles.getItems().clear();
            clientFiles.getItems().addAll(clientFilesList);
            serverFiles.getItems().addAll(serverFilesList);
        });

    }


    @FXML
    public void copyToServ(ActionEvent actionEvent) {
    }

/*    public void sendFileInfo(String filename) {
        try {

            String path = String.format("clientShell/clientDirectory/"+filename);
            File file = new File(path);

            long bytes =Files.size(file.toPath());
            long kbBytes=bytes / 1024;
            if(bytes%1024!=0) {
                kbBytes++ ;
            }
            sendCommand(String.format("/SEND "+kbBytes+" "+filename));
            //List<String> outList = new ArrayList<>(Arrays.asList("test","test","test","test"));



        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    private void sendFile(String filename){


     /*   byte[] b = new byte[1024];
        String path = String.format("clientShell/clientDirectory/"+filename);
        File file = new File(path);
        try{
            OutputStream dout = new DataOutputStream(out);
            InputStream ins = new FileInputStream(file);
            int n = ins.read(b);
            while (n != -1) {
                dout.write(b);
                dout.flush();
                n = ins.read(b);
            }

            DataInputStream din = new DataInputStream(in);
            String confirm;
            while (true) {
                confirm = din.readUTF();
                if (confirm.equals("/OK")) {
                    break;
                }
            }*/

       /* }catch (IOException e){
            e.printStackTrace();
        }*/
    }

    public void getFilesList() {

        try {
            File folder = new File("./clientShell/clientDirectory");
            System.out.println(Arrays.asList(folder.list()));

            String[] files = folder.list();
            if (clientFilesList == null) {
                clientFilesList = new ArrayList<>();
            } else {
                clientFilesList.clear();
            }
            for (String fileName : files) {
                clientFilesList.add(fileName);
            }



        } catch (NullPointerException e) {

            System.out.println("Каталог пользователя не найден");
        }


    }
}
