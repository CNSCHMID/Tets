package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.message.ServerInternalMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.*;

/**
 * Mapping vom authentication event bus calls to user management calls
 *
 * @author Marco Grawunder
 */
public class AuthenticationService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(AuthenticationService.class);

    /**
     * The list of current logged in users
     */
    final private Map<Session, User> userSessions = new HashMap<>();

    private final UserManagement userManagement;

    @Inject
    public AuthenticationService(EventBus bus, UserManagement userManagement) {
        super(bus);
        this.userManagement = userManagement;
    }

    public Optional<Session> getSession(User user) {
        Optional<Map.Entry<Session, User>> entry = userSessions.entrySet().stream().filter(e -> e.getValue().equals(user)).findFirst();
        return entry.map(Map.Entry::getKey);
    }

    public List<Session> getSessions(Set<User> users) {
        List<Session> sessions = new ArrayList<>();
        users.forEach(u -> {
            Optional<Session> session = getSession(u);
            if (session.isPresent()) sessions.add(session.get());
        });
        return sessions;
    }

    @Subscribe
    public void onLoginRequest(LoginRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new auth message with " + msg.getUsername() + " " + msg.getPassword());
        }
        ServerInternalMessage returnMessage;
        try {
            User newUser = userManagement.login(msg.getUsername(), msg.getPassword());
            returnMessage = new ClientAuthorizedMessage(newUser);
            Session newSession = UUIDSession.create();
            userSessions.put(newSession, newUser);
            returnMessage.setSession(newSession);
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new ServerExceptionMessage(new LoginException("Cannot auth user " + msg.getUsername()));
        }
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
        }
        post(returnMessage);
    }

    @Subscribe
    public void onLogoutRequest(LogoutRequest msg) {
        User userToLogOut = userSessions.get(msg.getSession().get());

        // Could be already logged out
        if (userToLogOut != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Logging out user " + userToLogOut.getUsername());
            }

            userManagement.logout(userToLogOut);
            userSessions.remove(msg.getSession());

            ServerMessage returnMessage = new UserLoggedOutMessage(userToLogOut.getUsername());
            post(returnMessage);

        }

    }

    @Subscribe
    public void onRetrieveAllOnlineUsersRequest(RetrieveAllOnlineUsersRequest msg) {
        AllOnlineUsersResponse response = new AllOnlineUsersResponse(userSessions.values());
        response.initWithMessage(msg);
        post(response);
    }


}
