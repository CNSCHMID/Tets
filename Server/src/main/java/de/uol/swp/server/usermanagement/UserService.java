package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.exception.SecurityException;
import de.uol.swp.common.message.AbstractMessage;
import de.uol.swp.common.user.IUserService;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.command.LoginCommand;
import de.uol.swp.common.user.command.LogoutCommand;
import de.uol.swp.common.user.message.LoginSuccessfulMessage;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import io.netty.channel.ChannelHandlerContext;

import javax.security.auth.login.LoginException;
import java.util.*;

/**
 * Class encapsulates all user specific server services
 *
 * @author Marco Grawunder
 */
public class UserService implements IUserService {

    private final EventBus bus;
    /**
     * The list of current logged in users
     */
    final private List<String> users = new ArrayList<>();
    final private Map<Session, String> userSessions = new HashMap<>();

    public UserService(EventBus bus) {
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe
    private void processLoginCommand(LoginCommand msg) {
        System.out.println("Got new login message with " + msg.getUsername() + " " + msg.getPassword());
        Session newSession = userService.login(msg.getUsername(), msg.getPassword());

        if (newSession.isValid()) {
            sendToClient(ctx, new LoginSuccessfulMessage(msg.getUsername()));
            putSession(ctx, newSession);
            // Send all clients information, that a new user is logged in
            sendToAll(new UserLoggedInMessage(msg.getUsername()));
        } else {
            sendToClient(ctx, new ExceptionMessage(new LoginException()));
        }
    }

    @Subscribe
    private void processLogoutCommand(LogoutCommand msg) {
        if (msg.getInfo() instanceof ChannelHandlerContext) {
            ChannelHandlerContext ctx = (ChannelHandlerContext) msg.getInfo();
            System.out.println("Got new logout " + msg.getSession());
            checkLogin(ctx, msg);
            removeUser(ctx, msg.getSession());
        }
    }

    private void checkLogin(ChannelHandlerContext ctx, AbstractMessage msg) {
        msg.forceSession();
        if (!msg.getSession().equals(getSession(ctx))) {
            throw new SecurityException("Login required for " + msg);
        }
    }

    private void removeUser(ChannelHandlerContext ctx, Session session) {
        String user = userService.logout(session);
        if (user != null) {
            UserLoggedOutMessage loggedOutMessage = new UserLoggedOutMessage(user);
            sendToAll(loggedOutMessage);
        }
    }


    @Override
    public Session login(String username, String password) {
        Session newSession;
        if (isValidLogin(username, password)) {
            newSession = new Session();
            this.users.add(username);
            this.userSessions.put(newSession, username);
            System.out.println("Logging in user with Session " + newSession);
        } else {
            newSession = Session.invalid;
        }
        return newSession;
    }

    public String logout(Session session) {
        if (session.isValid()) {
            String user = userSessions.get(session);
            users.remove(user);
            return user;
        }
        return null;
    }

    private boolean isValidLogin(String username, String password) {
        // TODO: Call real logic
        return username.equalsIgnoreCase(password);
    }

    @Override
    public List<String> retrieveAllUsers(Session session) {
        if (session.isValid()) {
            return Collections.unmodifiableList(users);
        }
        throw new SecurityException("Login Required!");
    }

}
