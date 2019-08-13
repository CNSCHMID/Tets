package de.uol.swp.client.user;

import de.uol.swp.common.user.IUserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginPresenter {

    public static final String fxml = "/fxml/LoginView.fxml";
    private static IUserService userService;

    public static void setUserService(IUserService us){
        userService = us;
    }

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    private void onLoginButtonPressed(ActionEvent event) {
        System.out.println("NUR EIN TEST");


        userService.login(loginField.getText(), passwordField.getText());
    }

    @FXML
    private void onRegisterButtonPressed(ActionEvent event) {

    }

}
