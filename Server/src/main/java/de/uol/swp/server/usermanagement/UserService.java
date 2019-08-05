package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.exception.SecurityException;
import de.uol.swp.common.message.IMessage;
import de.uol.swp.common.user.ISession;
import de.uol.swp.common.user.IUser;
import de.uol.swp.common.user.IUserService;
import de.uol.swp.common.user.command.LoginCommand;
import de.uol.swp.common.user.command.LogoutCommand;
import de.uol.swp.common.user.message.LoginSuccessfulMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.server.communication.Session;
import io.netty.channel.ChannelHandlerContext;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping vom event bus calls to user management calls
 *
 * @author Marco Grawunder
 */
public class UserService {

    private final EventBus bus;
    /**
     * The list of current logged in users
     */
    final private Map<Session, String> userSessions = new HashMap<>();

    private final UserManagement userManagement;

    public UserService(EventBus bus, IUserStore userStore) {
        this.userManagement = new UserManagement(userStore);
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe
    private void processLoginCommand(LoginCommand msg) {
        System.out.println("Got new login message with " + msg.getUsername() + " " + msg.getPassword());
        IUser newSession = userManagement.login(msg.getUsername(), msg.getPassword());

        // TODO: who created the session?
        // TODO: Exceptions on eventbus?

        final IMessage returnMessage;
        if (newSession.isValid()) {
            returnMessage = new LoginSuccessfulMessage(msg.getUsername());
            returnMessage.setSession(newSession);
        } else {
            returnMessage = new ExceptionMessage(new LoginException());
        }
        bus.post(returnMessage);
    }

    @Subscribe
    private void processLogoutCommand(LogoutCommand msg) {
        if (msg.getInfo() instanceof ChannelHandlerContext) {
            ChannelHandlerContext ctx = (ChannelHandlerContext) msg.getInfo();
            System.out.println("Got new logout " + msg.getSession());
            removeUser(ctx, msg.getSession());
        }
    }


    private void removeUser(ChannelHandlerContext ctx, ISession session) {
        String user = logout(session);
        if (user != null) {
            UserLoggedOutMessage loggedOutMessage = new UserLoggedOutMessage(user);
            bus.post(loggedOutMessage);
        }
    }




}
