package com.java.cloudStore.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML
    public TextField passField;
    @FXML
    public TextField loginField;
    @FXML
    public Label msgLabel;

    @FXML
    public CheckBox toggleCheck;
    @FXML
    public Button authBtn;
    @FXML
    public Button regBtn;
    private Controller controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        regBtn.setVisible(false);
        authBtn.setVisible(true);

    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    public void tryToAuth(ActionEvent actionEvent) {
        controller.authenticate(loginField.getText(), passField.getText());
    }

    public void getAuthMessage(String msg) {

        Platform.runLater(() -> msgLabel.setText(msg));
        msgLabel.setText("");
        loginField.clear();
        passField.clear();
    }

    public void getRegMessage(boolean success, String msg) {

        Platform.runLater(() -> msgLabel.setText(msg));
        msgLabel.setText("");
        if (success){
            toggleCheck.setSelected(false);
        }


    }

    @FXML
    public void toggleButton(ActionEvent actionEvent) {
        authBtn.setVisible(!toggleCheck.isSelected());
        regBtn.setVisible(toggleCheck.isSelected());



    }

    @FXML
    public void getReg(ActionEvent actionEvent) {
        controller.registrate(loginField.getText(), passField.getText());
    }
}
