package de.uol.swp.client.auth;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginPresenter extends AbstractPresenter {

    private static final Logger LOG = LogManager.getLogger(LoginPresenter.class);

    public static final String fxml = "/fxml/LoginView.fxml";

    private static final ShowRegistrationViewEvent showRegViewMessage = new ShowRegistrationViewEvent();

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    public LoginPresenter() {
    }

    @FXML
    private void onLoginButtonPressed(ActionEvent event) {
        userService.login(loginField.getText(), passwordField.getText());
    }

    @FXML
    private void onRegisterButtonPressed(ActionEvent event) {
        eventBus.post(showRegViewMessage);
    }
}
