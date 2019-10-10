package de.uol.swp.client.user;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;
import de.uol.swp.common.user.request.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    EventBus bus = new EventBus();
    CountDownLatch lock = new CountDownLatch(1);
    Object event;

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    private void loginUser() throws InterruptedException {
        UserService userService = new UserService(bus);
        userService.login(defaultUser.getUsername(), defaultUser.getPassword());
        lock.await(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    void loginTest() throws InterruptedException {
        loginUser();

        assertTrue(event instanceof LoginRequest);

        LoginRequest loginRequest = (LoginRequest) event;
        assertEquals(loginRequest.getUsername(), defaultUser.getUsername());
        assertEquals(loginRequest.getPassword(), defaultUser.getPassword());
    }

    @Test
    void isLoggedInTest() {
        UserService userService = new UserService(bus);
        assertThrows(UnsupportedOperationException.class, () -> userService.isLoggedIn(defaultUser));
    }

    @Test
    void logoutTest() throws InterruptedException {
        loginUser();
        event = null;

        UserService userService = new UserService(bus);
        userService.logout(defaultUser);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof LogoutRequest);

        LogoutRequest request = (LogoutRequest) event;

        assertEquals(request.authorizationNeeded(), true);
    }

    @Test
    void createUserTest() throws InterruptedException {
        UserService userService = new UserService(bus);
        userService.createUser(defaultUser);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RegisterUserRequest);

        RegisterUserRequest request = (RegisterUserRequest) event;

        assertEquals(request.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(request.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(request.getUser().getEMail(), defaultUser.getEMail());
        assertEquals(request.authorizationNeeded(), false);

    }

    @Test
    void updateUserTest() throws InterruptedException {
        UserService userService = new UserService(bus);
        userService.updateUser(defaultUser);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UpdateUserRequest);

        UpdateUserRequest request = (UpdateUserRequest) event;

        assertEquals(request.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(request.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(request.getUser().getEMail(), defaultUser.getEMail());
        assertEquals(request.authorizationNeeded(), true);
    }

    @Test
    void dropUserTest() {
        UserService userService = new UserService(bus);
        userService.dropUser(defaultUser);

        // TODO: Add when method is implemented
    }

    @Test
    void retrieveAllUsersTest() throws InterruptedException {
        UserService userService = new UserService(bus);
        userService.retrieveAllUsers();

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveAllOnlineUsersRequest);
    }

}