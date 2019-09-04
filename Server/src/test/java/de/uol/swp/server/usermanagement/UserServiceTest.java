package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    static final User userToRegister = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final User userWithSameName = new UserDTO("Marco", "Marco2", "Marco2@Grawunder.com");

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final UserService userService = new UserService(bus, userManagement);

    @Test
    void registerUserTest() {
        final RegisterUserRequest request = new RegisterUserRequest(userToRegister);

        // The post will lead to a call of a UserService function
        bus.post(request);

        // can only test, if something in the state has changed
        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        assertNotNull(loggedInUser);
        assertEquals(loggedInUser, userToRegister);
    }

    @Test
    void registerSecondUserWithSameName() {
        final RegisterUserRequest request = new RegisterUserRequest(userToRegister);
        final RegisterUserRequest request2 = new RegisterUserRequest(userWithSameName);

        bus.post(request);
        bus.post(request2);

        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        // old user should be still in the store
        assertNotNull(loggedInUser);
        assertEquals(loggedInUser, userToRegister);

        // old user should not be overwritten!
        assertNotEquals(loggedInUser.getEMail(), userWithSameName.getEMail());

    }

}