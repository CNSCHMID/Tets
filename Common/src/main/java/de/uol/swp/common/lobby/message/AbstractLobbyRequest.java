package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

public class AbstractLobbyRequest extends AbstractRequestMessage {

    String name;
    User user;

    public AbstractLobbyRequest() {
    }

    public AbstractLobbyRequest(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
