package de.uol.swp.common.lobby;

import de.uol.swp.common.user.User;

import java.util.Set;

public interface Lobby {

    String getName();

    void updateOwner(User user);

    User getOwner();

    void joinUser(User user);

    void leaveUser(User user);

    Set<User> getUsers();

}
