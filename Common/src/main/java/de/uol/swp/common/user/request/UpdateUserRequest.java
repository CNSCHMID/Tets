package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

public class UpdateUserRequest extends AbstractRequestMessage {

    final private User toUpdate;

    public UpdateUserRequest(User user){
        this.toUpdate = user;
    }

    public User getUser() {
        return toUpdate;
    }
}
