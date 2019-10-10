package de.uol.swp.client.register;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.common.user.UserService;
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

    public RegistrationPresenter() {
    }

    @Inject
    public RegistrationPresenter(EventBus eventBus, UserService userService) {
        setEventBus(eventBus);
    }

    @FXML
    void onCancelButtonPressed(ActionEvent event) {
        eventBus.post(registrationCanceledEvent);
    }

    @FXML
    void onRegisterButtonPressed(ActionEvent event) {
        if (Strings.isNullOrEmpty(loginField.getText())){
            eventBus.post(new RegistrationErrorEvent("Username cannot be empty"));
        }else if(!passwordField1.getText().equals(passwordField2.getText())) {
            eventBus.post(new RegistrationErrorEvent("Passwords are not equal"));
        } else if (Strings.isNullOrEmpty(passwordField1.getText())) {
            eventBus.post(new RegistrationErrorEvent("Password cannot be empty"));
        } else {
            userService.createUser(new UserDTO(loginField.getText(), passwordField1.getText(), "empty"));
        }
    }


}
