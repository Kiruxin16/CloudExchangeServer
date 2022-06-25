package com.java.cloudStore.client;

import com.java.cloudStore.api.ShareRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ShareController implements Initializable {

    @FXML
    public TextField userShareTxt;
    @FXML
    public Label shareMsg;

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    public void sendUserToShare(ActionEvent actionEvent) throws IOException {
        controller.sendUserName(userShareTxt.getText());

    }


    public void setMsgText(String msg) {
        Platform.runLater(()->shareMsg.setText(msg));
    }
}
