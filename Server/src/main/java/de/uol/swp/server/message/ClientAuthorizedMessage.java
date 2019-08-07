package de.uol.swp.server.message;

import de.uol.swp.common.user.IUser;

public class ClientAuthorizedMessage extends AbstractServerInternalMessage {

    private final IUser user;

    public ClientAuthorizedMessage(IUser user) {
        super();
        this.user = user;
    }

    public IUser getUser() {
        return user;
    }
}
