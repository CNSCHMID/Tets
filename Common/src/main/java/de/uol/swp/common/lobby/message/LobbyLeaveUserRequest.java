package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

public class LobbyLeaveUserRequest extends AbstractLobbyRequest {

    public LobbyLeaveUserRequest() {
    }

    public LobbyLeaveUserRequest(String lobbyName, User user) {
        super(lobbyName, user);
    }

}
