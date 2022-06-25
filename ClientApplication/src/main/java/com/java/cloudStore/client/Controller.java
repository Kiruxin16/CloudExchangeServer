package com.java.cloudStore.client;

import com.java.cloudStore.api.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    @FXML
    public TextField srvRmText;
    @FXML
    public TextField cliRmText;
    @FXML
    public Button shareBtn;



    private List<String> clientFilesList;
    private List<String> serverFilesList;
    private NetWork netWork;
    private Path currentClientDir;
    private boolean isAuthicated;

    private Stage authRegStage;
    private Stage shareStage;
    private AuthController authController;
    private String currentLogin;
    private String currentPath;
    private ShareController shareController;


    public void initialize(URL location, ResourceBundle resources) {

        clientFiles.getSelectionModel().selectedItemProperty().addListener(this::selectedIt);
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
                        authController.setAuthMessage(authResponse.getMessage());
                        setAuthicated(true);
                    }else{
                        authController.setAuthMessage(authResponse.getMessage());
                    }
                }else if(command instanceof RegResponse regResponse){
                    authController.getRegMessage(regResponse.isSuccess(),regResponse.getMessage());

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    public void selectedIt(ObservableValue observableValue, Object o, Object t1){
        if (clientFiles.getSelectionModel().getSelectedIndex()>=0){
            cl2.setDisable(false);
        }else{
            cl2.setDisable(true);
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
                }else if(command instanceof ShareResponse shareResponse){
                    if(shareResponse.isSuccess()){
                        Platform.runLater(()->{
                            shareStage.close();
                        });
                    }else {
                        shareController.setMsgText(shareResponse.getMsg());
                    }


                } else if (command instanceof StopMessage){
                    break;
                }
            }
            netWork.disconnect();
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

        }else{
            try{

                netWork.write(new StopMessage());
                isAuthicated=false;
                serverFiles.getItems().clear();
                clientFiles.getItems().clear();
                authBtn.setText("Login/Reg");
                nameLabel.setText("");
            }catch (IOException e){
                e.printStackTrace();
            }

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
        if(netWork==null||netWork.getSocket().isClosed()) {
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
        if(netWork==null||netWork.getSocket().isClosed()) {
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

    @FXML
    public void renameCli(ActionEvent actionEvent) {
        if (clientFiles.getSelectionModel().getSelectedIndex()>=0){
            cliRmText.setDisable(false);
            cliRmText.setText(clientFiles.getSelectionModel().getSelectedItem().toString());
            cliRmText.requestFocus();
        }
    }

    @FXML
    public void sendRenameCli(ActionEvent actionEvent) {
        new Thread(()->{
           try{
               Path oldName = currentClientDir.resolve(clientFiles.getSelectionModel().getSelectedItem().toString());
               Path newName = currentClientDir.resolve(cliRmText.getText());
               if(Files.exists(oldName)){
                   System.out.println("Hear");
               }
               Files.move(oldName,newName);
               refresh();
           }catch (Exception e){
               e.printStackTrace();
           }
           Platform.runLater(()->{
               cliRmText.clear();
               cliRmText.setDisable(true);
           });


        }).start();

    }

    @FXML
    public void renameSrv(ActionEvent actionEvent) {
        if (serverFiles.getSelectionModel().getSelectedIndex()>=0){
            srvRmText.setDisable(false);
            srvRmText.setText(serverFiles.getSelectionModel().getSelectedItem().toString());
            srvRmText.requestFocus();
        }
    }


    @FXML
    public void sendRenameSrv(ActionEvent actionEvent) throws IOException {

        netWork.write(new RenameRequest(serverFiles.getSelectionModel().getSelectedItem().toString(),srvRmText.getText()));
        srvRmText.clear();
        srvRmText.setDisable(true);
    }

    @FXML
    public void shareFile(ActionEvent actionEvent) {

        if (shareStage == null) {
            createShareStage();
        }
        shareStage.show();

    }

    private void createShareStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/share.fxml"));
            Parent root = fxmlLoader.load();

            shareStage = new Stage();

            shareStage.setScene(new Scene(root, 160, 45));
            shareController = fxmlLoader.getController();
            shareController.setController(this);

            shareStage.initStyle(StageStyle.UTILITY);
            shareStage.initModality(Modality.APPLICATION_MODAL);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    public void sendUserName(String name) throws IOException {
        netWork.write(new ShareRequest(name,serverFiles.getSelectionModel().getSelectedItem().toString()));
    }
}
