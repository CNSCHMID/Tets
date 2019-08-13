package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;

import java.util.*;

public class SimpleUserStore implements UserStore {

    Map<String, User> users = new HashMap<>();

    // FIXME: Remove after registration
    public SimpleUserStore(){
        createUser("test","test","test@test.de");
    }

    @Override
    public Optional<User> findUser(String username, String password) {
        Optional<User> usr = findUser(username);
        if (usr.isPresent() && usr.get().getPassword().equals(password)) {
            return usr;
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {
        User usr = users.get(username);
        if (usr != null) {
            return Optional.of(usr);
        }
        return Optional.empty();
    }

    @Override
    public User createUser(String username, String password, String eMail) {
        User usr = new UserDTO(username, password, eMail);
        users.put(username, usr);
        return usr;
    }

    @Override
    public User updateUser(String username, String password, String eMail) {
        return createUser(username, password, eMail);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();
        users.values().stream().forEach(u -> retUsers.add(u.getWithoutPassword()));
        return retUsers;
    }


}
