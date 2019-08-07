package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.IUser;

import java.util.List;
import java.util.Optional;

public interface IUserStore {
    Optional<IUser> findUser(String username, String password);
    Optional<IUser> findUser(String username);
    IUser createUser(String username, String password, String eMail);
    IUser updateUser(String username, String password, String eMail);
    List<IUser> getAllUsers();
}
