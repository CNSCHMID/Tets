package de.uol.swp.server.message;

import de.uol.swp.common.user.User;

public class ClientAuthorizedMessage extends AbstractServerInternalMessage {

    private final User user;

    public ClientAuthorizedMessage(User user) {
        super();
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
