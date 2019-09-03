package de.uol.swp.client.user;

import com.google.common.base.Strings;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.events.RegistationErrorEvent;
import de.uol.swp.client.events.RegistrationCanceledEvent;
import de.uol.swp.common.user.dto.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RegistrationView.fxml";

    private static final RegistrationCanceledEvent registrationCanceledEvent = new RegistrationCanceledEvent();

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    void onCancelButtonPressed(ActionEvent event) {
        eventBus.post(registrationCanceledEvent);
    }

    @FXML
    void onRegisterButtonPressed(ActionEvent event) {
        if (!passwordField1.getText().equals(passwordField2.getText())) {
            eventBus.post(new RegistationErrorEvent("Passwords are not equal"));
        } else if (Strings.isNullOrEmpty(passwordField1.getText())) {
            eventBus.post(new RegistationErrorEvent("Password cannot be empty"));
        } else {
            userService.createUser(new UserDTO(loginField.getText(), passwordField1.getText(), "empty"));
        }
    }


}
