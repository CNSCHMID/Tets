package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStore {
    Optional<User> findUser(String username, String password);
    Optional<User> findUser(String username);
    User createUser(String username, String password, String eMail);
    User updateUser(String username, String password, String eMail);
    List<User> getAllUsers();
}
