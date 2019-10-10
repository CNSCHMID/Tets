package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.response.RegistrationSuccessfulEvent;
import de.uol.swp.server.AbstractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mapping vom event bus calls to user management calls
 *
 * @author Marco Grawunder
 */
public class UserService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);

    private final UserManagement userManagement;

    @Inject
    public UserService(EventBus eventBus, UserManagement userManagement) {
        super(eventBus);
        this.userManagement = userManagement;
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
        }
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
        }
        post(returnMessage);
    }
}
