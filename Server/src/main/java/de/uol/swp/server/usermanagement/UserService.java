package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;

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
