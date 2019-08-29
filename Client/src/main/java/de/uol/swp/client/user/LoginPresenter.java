package de.uol.swp.client.user;

import de.uol.swp.client.AbstractPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginPresenter extends AbstractPresenter {

    private static final Logger LOG = LogManager.getLogger(LoginPresenter.class);

    public static final String fxml = "/fxml/LoginView.fxml";
    public Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    private void onLoginButtonPressed(ActionEvent event) {
        userService.login(loginField.getText(), passwordField.getText());
    }

    @FXML
    private void onRegisterButtonPressed(ActionEvent event) {

    }
}
