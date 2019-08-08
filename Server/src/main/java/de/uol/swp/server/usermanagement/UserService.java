package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.user.ISession;
import de.uol.swp.common.user.IUser;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.server.communication.Session;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.IServerInternalMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.usermanagement.store.IUserStore;
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

    static final Logger LOG = LogManager.getLogger(UserService.class);

    private final EventBus bus;

    /**
     * The list of current logged in users
     */
    final private Map<ISession, IUser> userSessions = new HashMap<>();

    private final UserManagement userManagement;

    public UserService(EventBus bus, IUserStore userStore) {
        this.userManagement = new UserManagement(userStore);
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe
    private void onLoginRequest(LoginRequest msg) {
        if (LOG.isDebugEnabled()){
            LOG.debug("Got new login message with " + msg.getUsername() + " " + msg.getPassword());
        }
        IServerInternalMessage returnMessage;
        try {
            IUser newUser = userManagement.login(msg.getUsername(), msg.getPassword());
            returnMessage = new ClientAuthorizedMessage(newUser);
            ISession newSession = Session.create();
            userSessions.put(newSession,newUser);
            returnMessage.setSession(newSession);
        }catch (Exception e){
            LOG.error(e);
            returnMessage = new ServerExceptionMessage(new LoginException("Cannot login user "+msg.getUsername()));
            returnMessage.setSession(msg.getSession());
        }
        bus.post(returnMessage);
    }

    @Subscribe
    private void onLogoutRequest(LogoutRequest msg) {
        IUser userToLogOut = userSessions.get(msg.getSession());

        // Could be already logged out
        if (userToLogOut != null){

            if (LOG.isDebugEnabled()){
                LOG.debug("Logging out user " + userToLogOut.getUsername());
            }

            userManagement.logout(userToLogOut);
            userSessions.remove(msg.getSession());
        }

    }


}
