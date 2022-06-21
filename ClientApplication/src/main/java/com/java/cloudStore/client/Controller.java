package com.java.cloudStore.client;

import com.java.cloudStore.api.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    public Button toServerButton;

    @FXML
    public ListView serverFiles;

    @FXML
    public ListView clientFiles;

    @FXML
    public Button cl2;

    @FXML
    public Button cl1;

    @FXML
    public Button srv2;
    @FXML
    public Button srv1;
    @FXML
    public Label nameLabel;
    @FXML
    public Button authBtn;

    private List<String> clientFilesList;
    private List<String> serverFilesList;
    private NetWork netWork;
    private Path currentClientDir;
    private boolean isAuthicated;

    private Stage authRegStage;
    private AuthController authController;
    private String currentLogin;
    private String currentPath;


    public void initialize(URL location, ResourceBundle resources) {

        currentClientDir = Path.of(System.getProperty("user.home"));
        clientFilesList = new ArrayList<>();
        setAuthicated(false);


    }

    private void setAuthicated(boolean authicated){
        this.isAuthicated=authicated;
    }

    private void authLoop(){
        try{
            while (!isAuthicated){
                CloudMessage command = netWork.read();
                if(command instanceof AuthResponse authResponse){
                    if(authResponse.isSuccess()){
                        netWork.write(new NavigateMessage(currentLogin));
                        Platform.runLater(()->{
                            nameLabel.setText(currentLogin);
                            authBtn.setText("log of");
                            authRegStage.close();
                        });
                        setAuthicated(true);
                    }else{
                        authController.getAuthMessage(authResponse.getMessage());
                    }
                }else if(command instanceof RegResponse regResponse){
                    authController.getRegMessage(regResponse.isSuccess(),regResponse.getMessage());

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void listenForCommands() {
        try {
            while (true) {
                CloudMessage command = netWork.read();
                if (command instanceof ListFiles serverList) {
                    serverFilesList = serverList.getFileList();
                    refresh();
                } else if (command instanceof FileMessage fileMessage) {
                    Path downloadPath = currentClientDir.resolve(fileMessage.getName());
                    Files.write(downloadPath, fileMessage.getData());
                    refresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void refresh() {
        getFilesList();
        Platform.runLater(() -> {
            clientFiles.getItems().clear();
            serverFiles.getItems().clear();
            clientFiles.getItems().addAll(clientFilesList);
            serverFiles.getItems().addAll(serverFilesList);
        });

    }


    @FXML
    public void copyToServ(ActionEvent actionEvent) throws IOException {
        Path upPath = currentClientDir.resolve(clientFiles.getSelectionModel().getSelectedItem().toString());
        FileMessage upFile = new FileMessage(upPath);
        netWork.write(upFile);
        refresh();

    }

    @FXML
    public void copyFromServ(ActionEvent actionEvent) {
        try {
            FileRequest downloadRequest = new FileRequest(serverFiles.getSelectionModel().getSelectedItem().toString());
            netWork.write(downloadRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getFilesList() {

        try {
            clientFilesList = Files.list(currentClientDir).map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

        } catch (Exception e) {

            System.out.println("Not found");
        }


    }

    @FXML
    public void changeDirClient(ActionEvent actionEvent) {
        Path newDir;
        if (actionEvent.getSource() == cl1) {
            newDir = currentClientDir = currentClientDir.resolve("..");
            if (Files.isDirectory(newDir)) {
                currentClientDir = newDir.normalize();

            } else {
                System.out.println("is not a dir");
            }
        } else {
            newDir = currentClientDir.resolve(clientFiles.getSelectionModel().getSelectedItem().toString());
            if (Files.isDirectory(newDir)) {
                currentClientDir = newDir.normalize();
            } else {
                System.out.println("is not a dir");
            }

        }
        refresh();

    }

    @FXML
    public void changeDirServer(ActionEvent actionEvent) throws IOException {
        if (actionEvent.getSource() == srv1) {
            netWork.write(new NavigateMessage(".."));
        } else {
            netWork.write(new NavigateMessage(serverFiles.getSelectionModel().getSelectedItem().toString()));
        }
    }

    @FXML
    public void getAuth(ActionEvent actionEvent) {
        if (!isAuthicated) {
            if (authRegStage == null) {
                createRegStage();
            }
            authRegStage.show();


        }

    }



    private void createRegStage() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/regAuth.fxml"));
            Parent root = fxmlLoader.load();

            authRegStage = new Stage();
            authRegStage.setTitle("Login/Registration");
            authRegStage.setScene(new Scene(root, 300, 200));

            authController = fxmlLoader.getController();
            authController.setController(this);


            authRegStage.initStyle(StageStyle.UTILITY);
            authRegStage.initModality(Modality.APPLICATION_MODAL);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }



    private void workThread() {
        netWork = new NetWork();
        Thread work = new Thread(() -> {

            authLoop();
            listenForCommands();
        });
        work.setDaemon(true);
        work.start();
    }

    public void authenticate(String login, String pass)  {
        if(netWork==null) {
            workThread();
        }
        this.currentLogin = login;
        this.currentPath = pass;
        try {
            netWork.write(new AuthRequest(login, pass));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void registrate(String login, String pass)  {
        if(netWork==null) {
            workThread();
        }
        this.currentLogin = login;
        this.currentPath = pass;
        try {
            netWork.write(new RegRequest(login, pass));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
