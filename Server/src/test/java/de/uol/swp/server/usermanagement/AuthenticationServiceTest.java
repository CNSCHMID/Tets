package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private CountDownLatch lock = new CountDownLatch(1);

    final User user = new UserDTO("name", "password", "email@test.de");
    final User user2 = new UserDTO("name2", "password2", "email@test.de2");
    final User user3 = new UserDTO("name3", "password3", "email@test.de3");


    final UserStore userStore = new MainMemoryBasedUserStore();
    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(userStore);
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    private Object event;

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

    @Test
    void loginTest() throws InterruptedException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        bus.post(loginRequest);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(userManagement.isLoggedIn(user));
        // is message send
        assertTrue(event instanceof ClientAuthorizedMessage);
        userManagement.dropUser(user);
    }

    @Test
    void loginTestFail() throws InterruptedException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword() + "äüö");
        bus.post(loginRequest);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertFalse(userManagement.isLoggedIn(user));
        assertTrue(event instanceof ServerExceptionMessage);
        userManagement.dropUser(user);
    }

    @Test
    void logoutTest() throws InterruptedException {
        loginUser(user);
        Optional<Session> session = authService.getSession(user);

        assertTrue(session.isPresent());
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSession(session.get());

        bus.post(logoutRequest);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertFalse(userManagement.isLoggedIn(user));
        assertTrue(event instanceof UserLoggedOutMessage);
    }

    private void loginUser(User userToLogin) {
        userManagement.createUser(userToLogin);
        final LoginRequest loginRequest = new LoginRequest(userToLogin.getUsername(), userToLogin.getPassword());
        bus.post(loginRequest);

        assertTrue(userManagement.isLoggedIn(userToLogin));
        userManagement.dropUser(userToLogin);
    }

    @Test
    void loggedInUsers() throws InterruptedException {
        loginUser(user);

        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        assertEquals(((AllOnlineUsersResponse) event).getUsers().size(), 1);
        assertEquals(((AllOnlineUsersResponse) event).getUsers().get(0), user);

    }

    // TODO: replace with parametrized test
    @Test
    void twoLoggedInUsers() throws InterruptedException {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        Collections.sort(users);

        users.forEach(u -> loginUser(u));

        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        List<User> returnedUsers = new ArrayList<>();
        returnedUsers.addAll(((AllOnlineUsersResponse) event).getUsers());

        assertEquals(returnedUsers.size(), 2);

        Collections.sort(returnedUsers);
        assertEquals(returnedUsers, users);

    }


    @Test
    void loggedInUsersEmpty() throws InterruptedException {
        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        assertTrue(((AllOnlineUsersResponse) event).getUsers().isEmpty());

    }

    @Test
    void getSessionsForUsersTest() {
        loginUser(user);
        loginUser(user2);
        loginUser(user3);
        Set<User> users = new TreeSet<>();
        users.add(user);
        users.add(user2);
        users.add(user3);


        Optional<Session> session1 = authService.getSession(user);
        Optional<Session> session2 = authService.getSession(user2);
        Optional<Session> session3 = authService.getSession(user2);

        assertTrue(session1.isPresent());
        assertTrue(session2.isPresent());
        assertTrue(session3.isPresent());

        List<Session> sessions = authService.getSessions(users);

        assertEquals(sessions.size(), 3);
        assertTrue(sessions.contains(session1.get()));
        assertTrue(sessions.contains(session2.get()));
        assertTrue(sessions.contains(session3.get()));

    }

}