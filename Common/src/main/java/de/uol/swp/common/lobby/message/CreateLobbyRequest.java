package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

public class CreateLobbyRequest extends AbstractLobbyRequest {

    public CreateLobbyRequest() {
    }

    public CreateLobbyRequest(String name, User owner) {
        super(name, owner);
    }

    public void setOwner(User owner) {
        setUser(owner);
    }

    public User getOwner() {
        return getUser();
    }

}
