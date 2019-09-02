package de.uol.swp.client.user;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.user.dto.UserDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MainPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/MainView.fxml";

    private static final Logger LOG = LogManager.getLogger(MainPresenter.class);

    private ObservableList<String> users;

    @FXML
    private ListView<String> usersView;

    @Subscribe
    public void newUser(UserLoggedInMessage message) {
        LOG.debug("New user "+message.getUsername()+" logged in");
        userService.retrieveAllUsers();
    }

    @Subscribe
    public void userLeft(UserLoggedOutMessage message) {
        LOG.debug("User "+message.getUsername()+" logged out");
        userService.retrieveAllUsers();
    }

    @Subscribe
    public void userList(AllOnlineUsersResponse allUsersResponse) {
        LOG.debug("Update of user list "+allUsersResponse.getUsers());
        updateUsersList(allUsersResponse.getUsers());
    }

    private void updateUsersList(List<UserDTO> userList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (users == null){
                users = FXCollections.observableArrayList();
                usersView.setItems(users);
            }
            users.clear();
            userList.forEach(u -> users.add(u.getUsername()));
        });
    }




}
