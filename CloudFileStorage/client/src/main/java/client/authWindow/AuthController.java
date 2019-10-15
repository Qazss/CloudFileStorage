package client.authWindow;

import client.Controllers;
import client.mainWindow.MainController;
import client.mainWindow.MainWindow;
import client.network.Network;
import common.Command;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


public class AuthController {

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    public void initialize(){
        Controllers.setAuthController(this);

        Thread t = new Thread(() -> {
            Network.getInstance().start();
        });
        t.setDaemon(true);
        t.start();
    }

    public void sendJoinRequest(){
        if(loginField.getText().isEmpty() || passwordField.getText().isEmpty()){
            new Alert(Alert.AlertType.WARNING, "Поле логина и пароля не может быть пустым!", ButtonType.CLOSE).showAndWait();
            return;
        }
        Network.getInstance().sendAuthCommand(loginField.getText(), passwordField.getText().hashCode(), Command.REGISTRATION);
    }

    public void sendLoginRequest(){
        if(loginField.getText().isEmpty() || passwordField.getText().isEmpty()){
            new Alert(Alert.AlertType.WARNING, "Поле логина и пароля не может быть пустым!", ButtonType.CLOSE).showAndWait();
            return;
        }
        Network.getInstance().sendAuthCommand(loginField.getText(), passwordField.getText().hashCode(), Command.AUTHORIZATION);
    }

    public void initMainWindow(){
        Network.setUserLogin(loginField.getText());

        Stage stage = new Stage();
        try {
            new MainWindow(stage);
            AuthWindow.getPrimaryStage().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
