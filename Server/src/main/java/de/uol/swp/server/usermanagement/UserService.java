package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.message.ServerInternalMessage;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping vom event bus calls to user management calls
 *
 * @author Marco Grawunder
 */
public class UserService {

    private final EventBus eventBus;
    private final UserManagement userManagement;

    public UserService(EventBus eventBus, UserManagement userManagement) {
        this.eventBus = eventBus;
        this.userManagement = userManagement;
    }
}
