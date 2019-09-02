package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

public class RegisterUserRequest extends AbstractRequestMessage {

    final private User toCreate;

    public RegisterUserRequest(User user){
        this.toCreate = user;
    }

    @Override
    public boolean authorizationNeeded() {
        return false;
    }

    public User getUser() {
        return toCreate;
    }
}
