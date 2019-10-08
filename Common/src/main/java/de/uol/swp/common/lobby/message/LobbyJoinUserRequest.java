package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

public class LobbyJoinUserRequest extends AbstractLobbyRequest {

    public LobbyJoinUserRequest() {
    }

    public LobbyJoinUserRequest(String lobbyName, User user) {
        super(lobbyName, user);
    }

}
