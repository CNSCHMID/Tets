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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping vom authentication event bus calls to user management calls
 *
 * @author Marco Grawunder
 */
public class AuthenticationService {
    private static final Logger LOG = LogManager.getLogger(AuthenticationService.class);

    private final EventBus bus;

    /**
     * The list of current logged in users
     */
    final private Map<Session, User> userSessions = new HashMap<>();

    private final UserManagement userManagement;

    public AuthenticationService(EventBus bus, UserManagement userManagement) {
        this.userManagement = userManagement;
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe
    private void onLoginRequest(LoginRequest msg) {
        if (LOG.isDebugEnabled()){
            LOG.debug("Got new login message with " + msg.getUsername() + " " + msg.getPassword());
        }
        ServerInternalMessage returnMessage;
        try {
            User newUser = userManagement.login(msg.getUsername(), msg.getPassword());
            returnMessage = new ClientAuthorizedMessage(newUser);
            Session newSession = de.uol.swp.server.communication.Session.create();
            userSessions.put(newSession,newUser);
            returnMessage.setSession(newSession);
        }catch (Exception e){
            LOG.error(e);
            returnMessage = new ServerExceptionMessage(new LoginException("Cannot login user "+msg.getUsername()));
            returnMessage.setSession(msg.getSession());
        }
        returnMessage.setMessageContext(msg.getMessageContext());
        bus.post(returnMessage);
    }

    @Subscribe
    private void onLogoutRequest(LogoutRequest msg) {
        User userToLogOut = userSessions.get(msg.getSession());

        // Could be already logged out
        if (userToLogOut != null){

            if (LOG.isDebugEnabled()){
                LOG.debug("Logging out user " + userToLogOut.getUsername());
            }

            userManagement.logout(userToLogOut);
            userSessions.remove(msg.getSession());

            // TODO: do we need to handle this message in Server, too?
            ServerMessage returnMessage = new UserLoggedOutMessage(userToLogOut.getUsername());
            bus.post(returnMessage);
        }

    }

    @Subscribe
    private void onRetrieveAllOnlineUsersRequest(RetrieveAllOnlineUsersRequest msg){
        AllOnlineUsersResponse response = new AllOnlineUsersResponse(userSessions.values());
        response.initWithMessage(msg);
        bus.post(response);
    }


}