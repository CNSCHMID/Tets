package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.response.RegistrationSuccessfulEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mapping vom event bus calls to user management calls
 *
 * @author Marco Grawunder
 */
public class UserService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);

    private final EventBus eventBus;
    private final UserManagement userManagement;

    @Inject
    public UserService(EventBus eventBus, UserManagement userManagement) {
        this.eventBus = eventBus;
        this.userManagement = userManagement;
        this.eventBus.register(this);
    }

    @Subscribe
    private void onRegisterUserRequest(RegisterUserRequest msg) {
        if (LOG.isDebugEnabled()){
            LOG.debug("Got new registration message with " + msg.getUser());
        }
        ResponseMessage returnMessage;
        try {
            User newUser = userManagement.createUser(msg.getUser());
            returnMessage = new RegistrationSuccessfulEvent();
        }catch (Exception e){
            LOG.error(e);
            returnMessage = new RegistrationExceptionMessage("Cannot create user "+msg.getUser()+" "+e.getMessage());
            returnMessage.setSession(msg.getSession());
        }
        returnMessage.setMessageContext(msg.getMessageContext());
        eventBus.post(returnMessage);
    }
}
