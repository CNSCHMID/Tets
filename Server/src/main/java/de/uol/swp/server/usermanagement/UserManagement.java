package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.IUser;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public class UserManagement extends AbstractUserManagement {

    final IUserStore userStore;
    final SortedSet<IUser> loggedInUsers = new TreeSet<>();

    public UserManagement(IUserStore userStore){
        this.userStore = userStore;
    }

    @Override
    public IUser login(String username, String password) {
        Optional<IUser> user = userStore.findUser(username, password);
        if (user.isPresent()){
            this.loggedInUsers.add(user.get());
            return user.get();
        }else{
            throw new SecurityException("Cannot login user "+username);
        }
    }

    public IUser createUser(IUser userToCreate){
        Optional<IUser> user = userStore.findUser(userToCreate.getUsername());
        if (user.isPresent()){
            throw new UserManagemtException("Username already used!");
        }
        return userStore.createUser(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());
    }

    public IUser updateUser(IUser userToUpdate){
        Optional<IUser> user = userStore.findUser(userToUpdate.getUsername());
        if (user.isEmpty()){
            throw new UserManagemtException("Username unknown!");
        }
        return userStore.updateUser(userToUpdate.getUsername(), userToUpdate.getPassword(), userToUpdate.getEMail());

    }

    @Override
    public void logout(IUser user) {
        boolean loggedOut = loggedInUsers.remove(user);
        // TODO: Should there be an exception in case of unsuccessful logout?
    }

    @Override
    public List<IUser> retrieveAllUsers() {
        return null;
    }
}
