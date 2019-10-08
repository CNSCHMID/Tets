package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;

public class UserLeftLobbyMessage extends AbstractLobbyMessage {

    public UserLeftLobbyMessage() {
    }

    public UserLeftLobbyMessage(String lobbyname, User user) {
        super(lobbyname, user);
    }
}
