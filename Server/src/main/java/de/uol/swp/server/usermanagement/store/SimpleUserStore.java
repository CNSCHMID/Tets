package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.IUser;
import de.uol.swp.common.user.User;

import java.util.*;

public class SimpleUserStore implements IUserStore {

    Map<String, IUser> users = new HashMap<>();

    @Override
    public Optional<IUser> findUser(String username, String password) {
        Optional<IUser> usr = findUser(username);
        if (usr.isPresent() && usr.get().getPassword().equals(password)) {
            return usr;
        }
        return Optional.empty();
    }

    @Override
    public Optional<IUser> findUser(String username) {
        IUser usr = users.get(username);
        if (usr != null) {
            return Optional.of(usr);
        }
        return Optional.empty();
    }

    @Override
    public IUser createUser(String username, String password, String eMail) {
        IUser usr = new User(username, password, eMail);
        users.put(username, usr);
        return usr;
    }

    @Override
    public IUser updateUser(String username, String password, String eMail) {
        return createUser(username, password, eMail);
    }

    @Override
    public List<IUser> getAllUsers() {
        List<IUser> retUsers = new ArrayList<>();
        users.values().stream().forEach(u -> retUsers.add(u.getWithoutPassword()));
        return retUsers;
    }


}
