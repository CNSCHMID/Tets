package de.uol.swp.client.user;

import com.google.common.base.Strings;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.user.dto.UserDTO;
import events.ShowLoginViewEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrationPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RegistrationView.fxml";
    private static final Logger LOG = LogManager.getLogger(RegistrationPresenter.class);

    private static final ShowLoginViewEvent showLoginViewEvent = new ShowLoginViewEvent();

    @FXML
    private TextField loginField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    void onCancelButtonPressed(ActionEvent event) {
        eventBus.post(showLoginViewEvent);
    }

    @FXML
    void onRegisterButtonPressed(ActionEvent event) {
        if (passwordField1.getText().equals(passwordField2.getText()) && !Strings.isNullOrEmpty(passwordField1.getText())){
            userService.createUser(new UserDTO(loginField.getText(), passwordField1.getText(),"empty"));
        }else{
            //TODO: Error message
            LOG.error("Fix entries.");
        }
    }


}
